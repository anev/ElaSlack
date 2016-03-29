package ru.evseev.elaslack

import akka.actor.{Actor, ActorLogging, Props}
import ru.evseev.elaslack.ElasticActor.{Config, DoReport, UnknownUserPhrase, UserPhrase}
import ru.evseev.elaslack.actors.SlackActor
import ru.evseev.elaslack.actors.SlackActor.{HiMessage, RawUserMessage, SlackMessage}

/**
  * Created by anev on 24/03/16.
  */
class RootActor(val appConfig: AppConfig) extends Actor with ActorLogging {

  val elasticCnf: Config = Config(appConfig.elasticUrl, appConfig.elasticUser, appConfig.elasticPass)

  val brainActor = context.actorOf(Props(classOf[BrainActor]), "brain")
  val storageActor = context.actorOf(Props(classOf[StorageActor]), "storage")
  val scheActor = context.actorOf(Props(classOf[ScheActor]), "sche")
  val elasticActor = context.actorOf(Props(classOf[ElasticActor], elasticCnf), "elastic")
  val slackActor = context.actorOf(Props(classOf[SlackActor], appConfig.slackToken, appConfig.slackChannel), "slack")

  slackActor ! HiMessage()

  override def receive: Receive = {

    case UnknownUserPhrase => slackActor ! HiMessage()

    case m: RawUserMessage => brainActor ! m

    case d: DoReport if !d.search.isDefined => storageActor ! d

    case d: DoReport if d.search.isDefined => scheActor ! d; slackActor ! d

    case m: UserPhrase => storageActor ! m; elasticActor ! m

    case m: SlackMessage => slackActor ! m

    case m: Any => log.warning(s"unknown message $m")
  }

}
