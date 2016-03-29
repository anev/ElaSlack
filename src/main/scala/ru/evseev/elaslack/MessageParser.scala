package ru.evseev.elaslack

import ru.evseev.elaslack.ElasticActor.{DoReport, SearchHits, UnknownUserPhrase, UserPhrase}

import scala.concurrent.duration.{Duration, FiniteDuration}

/**
  * Created by anev on 29/03/16.
  */
trait MessageParser extends IdGenerator {
  val Start = "elastic "

  val StartPattern = "(elastic )(.*)".r
  val ReportPattern1 = "(elastic report each)(.*)".r
  val ReportPattern2 = "(elastic report)(.*)".r

  val HiPattern1 = "(elastic hi)".r
  val HiPattern2 = "(elastic hello)".r
  val HiPattern3 = "(elastic help)".r

  val SincePtn = ".*since\\s([a-zA-Z0-9\\-/]+).*".r
  val TillPtn = ".*till\\s([a-zA-Z0-9\\-/]+).*".r

  def parse(msg: String): Option[UserPhrase] = {
    msg match {

      case ReportPattern1(prefix, body) => Some(DoReport(Duration(body).asInstanceOf[FiniteDuration]))

      case ReportPattern2(prefix, body) => Some(DoReport(Duration(body).asInstanceOf[FiniteDuration]))

      case StartPattern(prefix, body) => createSearchHits(msg)

      case StartPattern(prefix, body) => createSearchHits(msg)

      case s: String if s.startsWith(Start) => Some(UnknownUserPhrase)

      case _ => None
    }
  }

  def createSearchHits(s: String): Option[SearchHits] =
    goodStart(s).map { s => SearchHits(
      query = extractQuery(s),
      since = extractSince(s),
      till = extractTill(s),
      id = generateId
    )
    }

  private def goodStart(s: String) = s match {
    case s: String if s.startsWith(Start) => Some(s.replace(Start, "").trim)
    case _ => None
  }

  private def extractSince(s: String) = s match {
    case SincePtn(group) => group
    case _ => "now-1h"
  }

  private def extractTill(s: String) = s match {
    case TillPtn(group) => group
    case _ => "now"
  }

  private def extractQuery(prepStr: String): Map[String, String] = (for {
    cmd <- prepStr.split("\\s")
    if cmd.split(":").size == 2
  } yield {
    cmd.split(":")(0) -> cmd.split(":")(1)
  }).map(t => (t._1, t._2)).toMap
}
