package com.felicita._utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.felicita.actors._
import spray.json.DefaultJsonProtocol
import shapeless._


trait JsonSupport extends SprayJsonSupport {

  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat6(User)
  implicit val usersJsonFormat = jsonFormat1(Users)
  implicit val usersAlert = jsonFormat1(UsersActors.Alert)
}
