package com.felicita.actors

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.server.Directives.complete
import com.typesafe.config.ConfigFactory
import com.felicita.utils.FromMap.to
import com.felicita.utils.SQLiteHelpers

object SurveyRegistryActor {

  final case class Survey(id: Int, total_response_0: Double, user_id: Int)

  final case class Surveys(surveys: Vector[Survey])

  final case object GetSurveys

  final case class CreateSurvey(survey: Survey)

  final case class ActionPerformed(description: String)

  def props: Props = Props[SurveyRegistryActor]
}

class SurveyRegistryActor extends Actor with ActorLogging {

  import SurveyRegistryActor._

  val url: String = ConfigFactory.load().getString("url")
  println(s"My secret value is $url")

  def receive: Receive = {

    case GetSurveys =>
      """
        | Methods : Get
        | Path : /surveys
      """.stripMargin

      val request = SQLiteHelpers.request(url, "SELECT * FROM surveys", Seq("id", "total_response_0", "user_id"))
      request match {
        case Some(r) => val values = r.flatMap(s => to[Survey].from(s))
          sender() ! Surveys(values)
        case None => complete("mauvaise table")
      }

    case CreateSurvey(survey) =>
      """
        | Methods : Post
        | Path : /surveys
        |   {
        |	  "id": 0,
        |	  "total_response_0": 13.0,
        |	  "user_id": 23
        |   }
      """.stripMargin

      val query = s"INSERT INTO surveys(total_response_0,user_id) VALUES (${survey.total_response_0},${survey.user_id})"
      print(query)
      SQLiteHelpers.request(url, query, Seq("total_response_0", "user_id"))
      sender() ! ActionPerformed(s"Survey add (${survey.total_response_0}) created for users_id is ${survey.user_id}")
  }
}