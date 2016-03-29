package ru.evseev.elaslack.actors

import akka.actor.{Actor, ActorLogging}
import com.fasterxml.jackson.databind.JsonNode
import flowctrl.integration.slack.SlackClientFactory
import flowctrl.integration.slack.rtm.{Event, EventListener, SlackRealTimeMessagingClient}
import flowctrl.integration.slack.webapi.SlackWebApiClient
import ru.evseev.elaslack.ElasticActor.DoReport
import ru.evseev.elaslack.actors.SlackActor.{RawUserMessage, SlackMessage, TextMessage}


class SlackActor(val token: String, val channelName: String) extends Actor with ActorLogging {

  val rtmClient: SlackRealTimeMessagingClient = SlackClientFactory.createSlackRealTimeMessagingClient(token)

  val eventListener = new EventListener {

    override def handleMessage(jsonNode: JsonNode): Unit = {

      Some(jsonNode)
        .filter(isMessage)
        .map(_.get("text").asText())
        .map(context.parent ! RawUserMessage(_))

      def isMessage(j: JsonNode) = (
        j.get("type") != null
          && j.get("type").isTextual
          && j.get("type").asText() == "message"
          && j.get("text") != null
          && j.get("text").isTextual
        )
    }
  }

  rtmClient.addListener(Event.MESSAGE, eventListener)
  rtmClient.connect()

  val webApiClient: SlackWebApiClient = SlackClientFactory.createWebApiClient(token)

  override def receive: Receive = {

    case m: SlackMessage => post(m.toString)

    case DoReport(d, Some(s)) => self ! TextMessage(s"search `$s` will be repeated each `$d`")

  }

  def post(msg: String) = webApiClient.postMessage(channelName, msg, "ElasticBot", false)

}

object SlackActor {

  trait SlackMessage

  case class TextMessage(val text: String) extends SlackMessage {

    override def toString: String = text
  }

  case class HiMessage() extends SlackMessage {
    override def toString: String = "Hi! This is ElasticSearch bot.\n" +
      "I can search hits for you, usage examples\n" +
      " * `elastic name:api && domain:ps since now-2h/h till now/h` will perform the `name:api && domain:ps` query\n" +
      " * `elastic report each 1 minute` will perform previous query and repeat it periodically\n"
  }

  case class QueryResponseMessage(val count: Int, val query: String) extends SlackMessage {

    override def toString: String = s"found `$count` hits, query `$query`"
  }

  case class RawUserMessage(val text: String)

}
