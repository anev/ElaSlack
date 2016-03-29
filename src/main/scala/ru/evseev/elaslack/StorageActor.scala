package ru.evseev.elaslack

import akka.actor.{Actor, ActorLogging}
import ru.evseev.elaslack.ElasticActor.{DoReport, SearchHits}

import scala.concurrent.duration.Duration

/**
  * Created by anev on 29/03/16.
  */
class StorageActor extends Actor with ActorLogging {

  var lastSearch: SearchHits = _

  override def receive: Receive = {
    case DoReport(period: Duration, None) => {
      // todo save to elastic
      if (lastSearch != null) {
        sender ! DoReport(period, Some(lastSearch))
      }
    }
    case s: SearchHits => lastSearch = s
  }

}
