package ru.evseev.elaslack

import org.scalatest.{FlatSpec, GivenWhenThen, Matchers}
import ru.evseev.elaslack.ElasticActor.{DoReport, SearchHits}
import scala.concurrent.duration._

/**
  * Created by anev on 26/03/16.
  */
class ElasticActorTest extends FlatSpec with GivenWhenThen with Matchers with MessageParser {

  "BrainActor.SearchHits" should "create simple SearchHits" in {

    val s1 = parse("elastic name:alert")
    s1.isDefined should be(true)
    s1.get.asInstanceOf[SearchHits].query.size should be(1)
    s1.get.asInstanceOf[SearchHits].query("name") should be("alert")
  }

  it should "create SearchHits with several filters with space" in {

    val s1 = parse("elastic name:alert param:val1")
    s1.isDefined should be(true)
    s1.get.asInstanceOf[SearchHits].query.size should be(2)
    s1.get.asInstanceOf[SearchHits].query("name") should be("alert")
    s1.get.asInstanceOf[SearchHits].query("param") should be("val1")
  }

  it should "create SearchHits with several filters with 3 spacec" in {

    val s1 = parse("elastic   name:alert   param:val1")
    s1.isDefined should be(true)
    s1.get.asInstanceOf[SearchHits].query.size should be(2)
    s1.get.asInstanceOf[SearchHits].query("name") should be("alert")
    s1.get.asInstanceOf[SearchHits].query("param") should be("val1")
  }

  it should "create SearchHits with several filters with &&" in {

    val s1 = parse("elastic name:alert && param:val1")
    s1.isDefined should be(true)
    s1.get.asInstanceOf[SearchHits].query.size should be(2)
    s1.get.asInstanceOf[SearchHits].query("name") should be("alert")
    s1.get.asInstanceOf[SearchHits].query("param") should be("val1")
  }

  it should "create SearchHits with several filters with &" in {

    val s1 = parse("elastic name:alert1 & param:val1")
    s1.isDefined should be(true)
    s1.get.asInstanceOf[SearchHits].query.size should be(2)
    s1.get.asInstanceOf[SearchHits].query("name") should be("alert1")
    s1.get.asInstanceOf[SearchHits].query("param") should be("val1")
  }

  it should "create SearchHits with several filters with since and till" in {

    val s1 = parse("elastic name:alert1 param:val1 since now-3h till now")
    s1.isDefined should be(true)
    s1.get.asInstanceOf[SearchHits].query.size should be(2)
    s1.get.asInstanceOf[SearchHits].query("name") should be("alert1")
    s1.get.asInstanceOf[SearchHits].query("param") should be("val1")
    s1.get.asInstanceOf[SearchHits].since should be("now-3h")
    s1.get.asInstanceOf[SearchHits].till should be("now")
  }

  it should "create SearchHits with several filters with since and till 2" in {
    val s1 = parse("elastic name:alert1 param:val1 since now-5h till now-1h")
    s1.isDefined should be(true)
    s1.get.asInstanceOf[SearchHits].query.size should be(2)
    s1.get.asInstanceOf[SearchHits].query("name") should be("alert1")
    s1.get.asInstanceOf[SearchHits].query("param") should be("val1")
    s1.get.asInstanceOf[SearchHits].since should be("now-5h")
    s1.get.asInstanceOf[SearchHits].till should be("now-1h")
  }

  it should "create DoReport each 1 hour" in {
    val s1 = parse("elastic report each 1 hour")
    s1.isDefined should be(true)
    s1.get.asInstanceOf[DoReport].period should be(1 hour)
    s1.get.asInstanceOf[DoReport].search should be(None)
  }

  it should "elastic report each 1 minute" in {
    val s1 = parse("elastic report each 1 minute")
    s1.isDefined should be(true)
    s1.get.asInstanceOf[DoReport].period should be(1 minute)
    s1.get.asInstanceOf[DoReport].search should be(None)
  }
}
