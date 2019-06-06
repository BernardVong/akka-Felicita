package com.felicita._utils

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.felicita.actors.SubscribersActors.ActionPerformed
import com.felicita.actors.{Subscriber, Subscribers, User, Users}
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {

  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat6(User)
  implicit val usersJsonFormat = jsonFormat1(Users)

  implicit val subscriberJsonFormat = jsonFormat4(Subscriber)
  implicit val subscribersJsonFormat = jsonFormat1(Subscribers)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
