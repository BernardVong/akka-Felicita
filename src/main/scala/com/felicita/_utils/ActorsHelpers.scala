package com.felicita._utils

import com.felicita._utils.FromMap.to
import com.felicita.actors._
import com.typesafe.config.ConfigFactory
import spray.json.{JsObject, JsValue}



object ActorsHelpers {

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
          case "users" => Users(r.flatMap(v => to[User].from (v)))
          case "tips" => Tips(r.flatMap(v => to[Tip].from (v)))
        }
    }
  }
  def findById(table: String, fields: Seq[String], id: Any): Option[Any] =
    table match {
      case "users" => selectAll(table, fields).asInstanceOf[Users].users.find(_.id == id)
      case "tips" => selectAll(table, fields).asInstanceOf[Tips].tips.find(_.id == id)
    }

  /** QUERIES BUILDER END **/


  def headOrEmpty(seqUnknown: Seq[Any]): String =
  {
    seqUnknown.headOption match {
      case Some(value) => value.toString
      case None => "?"
    }
  }
}