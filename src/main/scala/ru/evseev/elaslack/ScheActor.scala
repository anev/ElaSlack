package ru.evseev.elaslack

import akka.actor.{Actor, ActorLogging}
import ru.evseev.elaslack.ElasticActor.DoReport

/**
  * Created by anev on 29/03/16.
  */
class ScheActor extends Actor with ActorLogging {

  import scala.concurrent.ExecutionContext.Implicits.global

  override def receive: Receive = {

    case DoReport(d, Some(s)) => context.system.scheduler.schedule(d, d, sender, s)
  }

}
