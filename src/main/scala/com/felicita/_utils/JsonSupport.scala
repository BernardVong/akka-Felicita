package com.felicita._utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.util.Tuple
import com.felicita._utils.helpers.ActorsHelpers.Alert
import com.felicita.actors._
import spray.json.DefaultJsonProtocol
import shapeless._


trait JsonSupport extends SprayJsonSupport {

  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat6(User)
  implicit val usersJsonFormat = jsonFormat1(Users)

  implicit val tipJsonFormat = jsonFormat3(Tip)
  implicit val tipsJsonFormat = jsonFormat1(Tips)
  implicit val tipsUsers = jsonFormat1(TipsUsers)

  implicit val giveawayJsonFormat = jsonFormat3(Giveaway)
  implicit val giveawaysJsonFormat = jsonFormat1(Giveaways)
  implicit val entryJsonFormat = jsonFormat3(Entry)
  implicit val entriesJsonFormat = jsonFormat1(Entries)

  implicit val surveyJsonFormat = jsonFormat4(Survey)
  implicit val surveysJsonFormat = jsonFormat1(Surveys)

  implicit val alertJsonFormat= jsonFormat1(Alert)
}
