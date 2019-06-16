package com.felicita.actors

import akka.actor.{Actor, ActorLogging, Props}
import com.felicita._utils.helpers.ActorsHelpers._
import com.felicita._utils.helpers.SQLiteHelpers
import com.typesafe.config.ConfigFactory
import spray.json.{JsObject, JsValue}


/* surveys classes */
final case class Survey(id: Int, question: String, response_1: Int, response_2: Int)
final case class Surveys(surveys: Vector[Survey])
/* surveys classes */


object SurveysActors {

  /* SURVEYS */
  final case object GetSurveys
  final case class CreateSurvey(survey: JsValue)
  final case class GetSurvey(id: Int)
  final case class DeleteSurvey(id: Int)
  final case class ResponseToSurvey(surveyId: Int, responseJson: JsValue)
  final case class GetSurveyResult(surveyId: Int)

  def props: Props = Props[SurveysActors]

  val db_table = "surveys"
  val db_path: String = ConfigFactory.load().getString("db_url")
  val db_fields: Seq[String] = Seq("question", "response_1", "response_2")
  val db_fields_with_id: Seq[String] = db_fields :+ "id"
}


class SurveysActors extends Actor with ActorLogging  {
  import SurveysActors._

  /** ROUTES **/
  override def receive: Receive = {

    /** CRUD **/
    case GetSurveys => sender() ! selectAll(db_table, db_fields_with_id)

    case CreateSurvey(request: JsValue) =>
      SQLiteHelpers.request(db_path, "INSERT INTO " + db_table + " (" + getFieldsFromJsonToString(request) + ") VALUES (" + getValuesFromJsonToString(request) + ")", db_fields)
      sender() ! Alert("Survey added.")

    case GetSurvey(id) =>
      val survey = findById(db_table, db_fields_with_id, id)
      sender() ! (if (survey.isDefined) survey.get else akka.actor.Status.Failure(AlertError("Survey not found")))

    case DeleteSurvey(id) =>
      val survey = findById(db_table, db_fields_with_id, id).asInstanceOf[Option[Survey]]
      if (survey.isDefined) {
        val query = "DELETE FROM " + db_table + " WHERE id='" + survey.get.id + "'"
        SQLiteHelpers.request(db_path, query, db_fields)
        sender() ! Alert("Survey removed.")
      }
      else sender() ! akka.actor.Status.Failure(AlertError("Survey not found"))

    case ResponseToSurvey(surveyId, responseJson) =>
      val userIdOption = responseJson.asInstanceOf[JsObject].getFields("user_id").headOption
      val responseOption = responseJson.asInstanceOf[JsObject].getFields("response").headOption

      if(userIdOption.isDefined)
      {
        val userOption = userIdOption match {
          case Some(userId) =>  findById(UsersActors.db_table, UsersActors.db_fields_with_id, userId.toString.toInt).asInstanceOf[Option[User]]
          case None => sender() ! akka.actor.Status.Failure(AlertError("User ID not found"))
        }

        (userOption, responseOption) match {
          case(Some(user), Some(response)) =>

            // increment response
            val survey = findById(db_table, db_fields_with_id, surveyId).asInstanceOf[Option[Survey]]
            val query = s"UPDATE $db_table SET response_${response.toString} = response_${response.toString} + 1 WHERE id=" + survey.get.id
            SQLiteHelpers.request(db_path, query, db_fields)
            sender() ! Alert("Response submited.")

          case(None, Some(response)) => sender() ! akka.actor.Status.Failure(AlertError("User not found"))
          case(Some(user), None) => sender() ! akka.actor.Status.Failure(AlertError("Survey not found"))

        }
      }
    case GetSurveyResult(surveyId) =>
      findById(db_table, db_fields_with_id, surveyId).asInstanceOf[Option[Survey]] match {
        case Some(survey) =>
          val winner = if(survey.response_1 > survey.response_2) 1 else 2
          sender() ! Alert(survey.question + " --- Response " +winner+ " is the winner ---")

        case None => sender() ! akka.actor.Status.Failure(AlertError("Survey not found"))
      }

    /** CRUD END **/


  }
  /** ROUTES END **/


}

