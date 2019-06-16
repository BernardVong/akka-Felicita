package com.felicita._utils.helpers

import com.felicita._utils.FromMap.to
import com.felicita.actors._
import com.typesafe.config.ConfigFactory
import spray.json.{JsObject, JsValue}

object ActorsHelpers {

  /** UTILS **/
  final case class Alert(message: String)
  final case class AlertError(message: String = "", cause: Throwable = None.orNull) extends Exception(message, cause)
  /** UTILS END **/


  /** FIELDS/VALUES **/
  def getFieldsFromCaseClassToString(caseClass: Any) : String = caseClass.getClass.getDeclaredFields.map(_.getName).mkString(",")
  def getFieldsFromJsonToString(json: JsValue) : String = json.asInstanceOf[JsObject].fields.keys.mkString(",").replaceAll("\"", "")
  def getValuesFromJsonToString(json: JsValue) : String = json.asInstanceOf[JsObject].fields.values.mkString("'","','","'").replaceAll("\"", "")
  def getValuesFromCaseClassToStringUser(instanceType: String, cc: Any) : String = {
    instanceType match {
      case "user" => cc.asInstanceOf[User].productIterator.to.mkString("'","','","'")
      case "users" => cc.asInstanceOf[Users].productIterator.to.mkString("'","','","'")
      case "users" => cc.asInstanceOf[Users].productIterator.to.mkString("'","','","'")
    }
  }
  /** FIELDS/VALUES END **/

  /** QUERIES BUILDER **/
  def selectAll(table: String, fields: Seq[String]): Any =
  {
    val request = SQLiteHelpers.request(ConfigFactory.load().getString("db_url"), "SELECT * FROM " + table, fields)
    request match {
      case Some(r) =>
        table match {
          case UsersActors.db_table => Users(r.flatMap(v => to[User].from (v)))
          case TipsActors.db_table => Tips(r.flatMap(v => to[Tip].from (v)))
          case GiveawaysActors.db_table => Giveaways(r.flatMap(v => to[Giveaway].from (v)))
          case GiveawaysActors.db_entries => Entries(r.flatMap(v => to[Entry].from (v)))
          case SurveysActors.db_table => Surveys(r.flatMap(v => to[Survey].from (v)))
        }
    }
  }
  def findById(table: String, fields: Seq[String], id: Any): Option[Any] =
    table match {
      case UsersActors.db_table => selectAll(table, fields).asInstanceOf[Users].users.find(_.id == id)
      case TipsActors.db_table => selectAll(table, fields).asInstanceOf[Tips].tips.find(_.id == id)
      case GiveawaysActors.db_table => selectAll(table, fields).asInstanceOf[Giveaways].giveaways.find(_.id == id)
      case GiveawaysActors.db_entries => selectAll(table, fields).asInstanceOf[Entries].entries.find(_.id == id)
      case SurveysActors.db_table => selectAll(table, fields).asInstanceOf[Surveys].surveys.find(_.id == id)

    }


  def SumTipsByUser(userId: Int): Double = selectAll(TipsActors.db_table, TipsActors.db_fields_with_id).asInstanceOf[Tips].tips.filter(_.user_id == userId).map(_.amount).sum
  /** QUERIES BUILDER END **/


  def headOrEmpty(seqUnknown: Seq[Any]): String =
  {
    seqUnknown.headOption match {
      case Some(value) => value.toString
      case None => "?"
    }
  }
}
