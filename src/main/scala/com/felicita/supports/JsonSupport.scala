package com.felicita.supports

import com.felicita.actors.UserRegistryActor._
import com.felicita.actors.SurveyRegistryActor._
import com.felicita.actors.GiveawayRegistryActor._
import com.felicita.actors.DonationRegistryActor._
import spray.json.RootJsonFormat
import com.felicita.actors.SurveyRegistryActor.SurveyActionPerformed
import com.felicita.actors.UserRegistryActor.UserActionPerformed
import com.felicita.actors.GiveawayRegistryActor.GiveawayActionPerformed
import com.felicita.actors.DonationRegistryActor.DonationActionPerformed

//#json-support
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport {

  import DefaultJsonProtocol._

  implicit val subscriberJsonFormat: RootJsonFormat[User] = jsonFormat6(User)
  implicit val subscribersJsonFormat: RootJsonFormat[Users] = jsonFormat1(Users)

  implicit val surveyJsonFormat: RootJsonFormat[Survey] = jsonFormat3(Survey)
  implicit val surveysJsonFormat: RootJsonFormat[Surveys] = jsonFormat1(Surveys)

  implicit val giveawayJsonFormat: RootJsonFormat[Giveaway] = jsonFormat2(Giveaway)
  implicit val giveawaysJsonFormat: RootJsonFormat[Giveaways] = jsonFormat1(Giveaways)

  implicit val donationJsonFormat: RootJsonFormat[Donation] = jsonFormat4(Donation)
  implicit val donationsJsonFormat: RootJsonFormat[Donations] = jsonFormat1(Donations)

  implicit val SurveyActionPerformedJsonFormat: RootJsonFormat[SurveyActionPerformed] = jsonFormat1(SurveyActionPerformed)
  implicit val UserActionPerformedJsonFormat: RootJsonFormat[UserActionPerformed] = jsonFormat1(UserActionPerformed)
  implicit val GiveawayActionPerformedJsonFormat: RootJsonFormat[GiveawayActionPerformed] = jsonFormat1(GiveawayActionPerformed)
  implicit val DonationActionPerformedJsonFormat: RootJsonFormat[DonationActionPerformed] = jsonFormat1(DonationActionPerformed)
}
