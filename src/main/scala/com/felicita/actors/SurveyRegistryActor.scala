package com.felicita.actors

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.server.Directives.complete
import com.typesafe.config.ConfigFactory
import utils.FromMap.to
import utils.{Database, SQLiteHelpers}
import scala.util.Properties

object SurveyRegistryActor {

  final case class Survey(id: String, total_response_0: Float)

  final case class Surveys(surveys: Vector[Survey])

  final case object GetSurveys

  def props: Props = Props[SurveyRegistryActor]
}

class SurveyRegistryActor extends Actor with ActorLogging {

  import SurveyRegistryActor._

  var subscribers = Set.empty[Survey]

  val database = new Database()

  def receive: Receive = {

    case GetSurveys =>
      val url = database.envOrElseConfig("url")
      println(s"My secret value is $url")
      val req = SQLiteHelpers.request(url, "SELECT * FROM survey", Seq("id", "total_response_0"))
      req match {
        case Some(r) => val values = r.flatMap(s => to[Survey].from(s))
          sender() ! Surveys(values)
        case None => complete("mauvaise table")
      }
  }
}

