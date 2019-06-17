package com.felicita.routes

import akka.actor.ActorRef
import akka.http.scaladsl.model.{HttpEntity, MediaTypes, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.{ByteString, Timeout}
import com.felicita._utils.JsonSupport
import com.felicita.actors._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._


class SurveysTestsSpecs extends WordSpec with Matchers with ScalatestRouteTest
  with SurveysRoutes with JsonSupport with ScalaFutures {

  override implicit val timeout: Timeout = Timeout(5.seconds)
  override val surveysActor :  ActorRef = system.actorOf(SurveysActors.props, "surveysActor")

  var id: Int = 99991
  var userId: Int = 1
  val surveyJson = ByteString(
    s"""{
          "id": "$id",
          "question": "is it a test ?",
          "response_1": 0,
          "response_2": 0
        }
        """.stripMargin)

  val surveyResponseSubmit = ByteString(
    s"""{
          "user_id": $userId,
          "response": 1
        }
        """.stripMargin)


  "Surveys API" should {

    "able to get all surveys - GET /surveys => return OK" in {
      Get("/surveys") ~> surveysRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to create a Survey - POST /surveys (json)" in {
      Post("/surveys", HttpEntity(MediaTypes.`application/json`, surveyJson)) ~> surveysRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to get a Survey - GET /surveys/{id}" in {
      Get(s"/surveys/$id") ~> surveysRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to participate to a Survey - POST /surveys/{id} (json)" in {
      Post(s"/surveys/$id", HttpEntity(MediaTypes.`application/json`, surveyResponseSubmit)) ~> surveysRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to get Survey result - GET /surveys/{id}/result" in {
      Get(s"/surveys/$id/result") ~> surveysRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to delete a Survey - DELETE /surveys/{id}" in {
      Delete(s"/surveys/$id") ~> surveysRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }


  }
}