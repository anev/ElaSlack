package ru.evseev.elaslack

import akka.actor.{Actor, ActorLogging}
import io.searchbox.client.config.HttpClientConfig
import io.searchbox.client.{JestClient, JestClientFactory}
import io.searchbox.core.Search
import org.elasticsearch.index.query.{BoolQueryBuilder, QueryBuilders}
import org.elasticsearch.search.builder.SearchSourceBuilder
import ru.evseev.elaslack.ElasticActor._
import ru.evseev.elaslack.actors.SlackActor.QueryResponseMessage

import scala.concurrent.duration._

/**
  * Created by anev on 24/03/16.
  */
class ElasticActor(val config: Config) extends Actor with ActorLogging {

  private var client: JestClient = _

  val factory: JestClientFactory = new JestClientFactory
  val httpConf = new HttpClientConfig.Builder(config.addr)
    .multiThreaded(false)
    .defaultCredentials(config.login, config.pass).build
  factory.setHttpClientConfig(httpConf)
  this.client = factory.getObject

  def initQ(sh: SearchHits) = QueryBuilders.boolQuery().must(
    QueryBuilders.rangeQuery("@timestamp").from(sh.since).to(sh.till))

  def addFilter(b: BoolQueryBuilder, t: (String, Any)) = b.must(QueryBuilders.matchQuery(t._1, t._2))

  override def receive: Receive = {

    case sh: SearchHits => {
      log.debug("searching in elastic: [{}]", sh.query)
      try {

        val filters = sh.query.foldLeft(initQ(sh))(addFilter)

        val searchSourceBuilder = new SearchSourceBuilder
        searchSourceBuilder.postFilter(filters)
        val search = new Search.Builder(searchSourceBuilder.toString).addIndex(config.index).build
        val result = client.execute(search)

        val found: Int = result.getTotal match {
          case null => 0
          case _ => result.getTotal
        }
        log.debug("found {}", result.getTotal)
        sender ! QueryResponseMessage(found, sh.toString)
      } catch {
        case e: Exception => {
          log.error(e.toString, e)
        }
      }
    }
  }
}

object ElasticActor {

  case object Tick

  case class Config(val addr: String, val login: String, val pass: String, val index: String = "logstash-*")

  trait UserPhrase

  case object UnknownUserPhrase extends UserPhrase

  case class DoReport(period: FiniteDuration = 1 hour, val search: Option[SearchHits] = None) extends UserPhrase

  case class SearchHits(val query: Map[String, Any],
                        val since: String = "now-1h",
                        val till: String = "now",
                        val id: Int) extends UserPhrase {

    override def toString: String = s"$id> [$since..$till] $query"
  }

}