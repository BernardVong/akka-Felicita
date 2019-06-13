package com.felicita._utils

import spray.json.{JsObject, JsValue}



object CaseClassHelpers {
  def getFieldsFromCaseClassToString(caseClass: Any) : String = caseClass.getClass.getDeclaredFields.map(_.getName).mkString(",")
  def getFieldsFromJsonToString(json: JsValue) : String = json.asInstanceOf[JsObject].fields.keys.mkString(",").replaceAll("\"", "")
  def getValuesFromJsonToString(json: JsValue) : String = json.asInstanceOf[JsObject].fields.values.mkString("'","','","'").replaceAll("\"", "")


  // TODO : transform to universal case class function : getValuesFromCaseClassToString(cc: AnyCC)
  import com.felicita.actors.User
  def getValuesFromCaseClassToStringUser(user: User) : String = user.productIterator.to.mkString("'","','","'")

}