package com.felicita.actors

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.server.Directives.complete
import com.typesafe.config.ConfigFactory
import utils.FromMap.to
import utils.{Database, SQLiteHelpers}
import scala.util.Properties

object SurveyRegistryActor {

  final case class Survey(id: String, total_response_0: Double)

  final case class Surveys(surveys: Vector[Survey])

  final case object GetSurveys

  final case class CreateSurvey(survey: Survey)

  final case class ActionPerformed(description: String)

  def props: Props = Props[SurveyRegistryActor]
}

class SurveyRegistryActor extends Actor with ActorLogging {

  import SurveyRegistryActor._

  val database = new Database()

  def receive: Receive = {

    case GetSurveys =>
      val url = database.envOrElseConfig("url")
      println(s"My secret value is $url")
      val request = SQLiteHelpers.request(url, "SELECT * FROM surveys", Seq("id", "total_response_0"))
      request match {
        case Some(r) => val values = r.flatMap(s => to[Survey].from(s))
          sender() ! Surveys(values)
        case None => complete("mauvaise table")
      }

    case CreateSurvey(survey) =>
      val url = database.envOrElseConfig("url")
      println(s"My secret value is $url")
      val query = s"INSERT INTO survey(id,total_response_0) VALUES (${survey.id},${survey.total_response_0})"
      val request = SQLiteHelpers.request(url, query, Seq("id", "total_response_0"))
      sender() ! ActionPerformed(s"Survey (${survey.id} ${survey.total_response_0})  created.")
  }
}