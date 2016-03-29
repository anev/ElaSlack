package ru.evseev.elaslack

import akka.actor.{Actor, ActorLogging}
import ru.evseev.elaslack.actors.SlackActor.RawUserMessage

/**
  * Created by anev on 24/03/16.
  */
class BrainActor extends Actor with ActorLogging with MessageParser {

  override def receive: Receive = {

    case RawUserMessage(msg) => {
      log.debug("trying to parse message [{}]", msg)
      parse(msg).map(sender ! _)
    }

  }

}
