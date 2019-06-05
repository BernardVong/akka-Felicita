package com.felicita

import com.felicita.SubscriberRegistryActor.ActionPerformed

//#json-support
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val subscriberJsonFormat = jsonFormat4(Subscriber)
  implicit val subscribersJsonFormat = jsonFormat1(Subscribers)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
//#json-support
