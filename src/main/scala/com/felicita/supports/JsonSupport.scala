package com.felicita.supports


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.felicita.actors.{Subscriber, Subscribers}
import com.felicita.actors.SubscribersActors.ActionPerformed
import spray.json.DefaultJsonProtocol


trait JsonSupport extends SprayJsonSupport {

  import DefaultJsonProtocol._

  implicit val subscriberJsonFormat = jsonFormat4(Subscriber)
  implicit val subscribersJsonFormat = jsonFormat1(Subscribers)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
