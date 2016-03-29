package ru.evseev.elaslack

import akka.actor.{ActorRef, ActorSystem, Props}

/**
  * Created by anev on 24/03/16.
  */
object App {

  val as: ActorSystem = ActorSystem("ElaSlack")
  var rootActor: ActorRef = _

  def main(args: Array[String]) {
    parser.parse(args, AppConfig()) match {
      case Some(config) => rootActor = as.actorOf(Props(classOf[RootActor], config), "root")
      case None => System.exit(-1)
    }
  }

  val parser = new scopt.OptionParser[AppConfig]("scopt") {
    head("scopt", "3.x")
    opt[String]('t', "slackToken") required() valueName ("<slackToken>") action { (x, c) =>
      c.copy(slackToken = x)
    } text ("slack API token")
    opt[String]('c', "slackChannel") valueName ("<slackChannel>") action { (x, c) =>
      c.copy(slackChannel = x)
    } text ("slack slackChannel, default is #elastic")
    opt[String]('e', "elasticUrl") required() valueName ("<elasticUrl>") action { (x, c) =>
      c.copy(elasticUrl = x)
    } text ("slack API token")
    opt[String]('u', "elasticUser") required() valueName ("<elasticUser>") action { (x, c) =>
      c.copy(elasticUser = x)
    } text ("slack API token")
    opt[String]('p', "elasticPass") required() valueName ("<elasticPass>") action { (x, c) =>
      c.copy(elasticPass = x)
    } text ("slack API token")
    note("some notes.\n")
    help("help") text ("prints this usage text")
  }
}

case class AppConfig(val slackToken: String = "",
                     val slackChannel: String = "#elastic",
                     val elasticUrl: String = "",
                     val elasticUser: String = "",
                     val elasticPass: String = ""
                    )