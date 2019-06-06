package com.felicita.supports

import com.felicita.actors.SubscriberRegistryActor._
import com.felicita.actors.SurveyRegistryActor._
import spray.json.RootJsonFormat
import com.felicita.actors.SurveyRegistryActor.ActionPerformed

//#json-support
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {
  import DefaultJsonProtocol._

implicit val subscriberJsonFormat: RootJsonFormat[Subscriber] = jsonFormat4(Subscriber)
implicit val subscribersJsonFormat: RootJsonFormat[Subscribers] = jsonFormat1(Subscribers)

implicit val surveyJsonFormat: RootJsonFormat[Survey] = jsonFormat2(Survey)
implicit val surveysJsonFormat: RootJsonFormat[Surveys] = jsonFormat1(Surveys)

implicit val actionPerformedJsonFormat: RootJsonFormat[ActionPerformed] = jsonFormat1(ActionPerformed)
}
