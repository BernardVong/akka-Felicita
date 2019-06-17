package com.felicita.routes

import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, MediaTypes, MessageEntity, Multipart, StatusCodes}
import akka.http.scaladsl.server._
import Directives._
import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.{Marshal, Marshaller}
import akka.http.scaladsl.model.Multipart.FormData
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.{ByteString, Timeout}
import com.felicita._utils.JsonSupport
import com.felicita.actors.TipsActors.CreateTip
import com.felicita.actors._
import com.felicita.routes._
import org.scalatest.concurrent.ScalaFutures
import spray.json._

import scala.concurrent.duration._
import scala.util.parsing.json.{JSON, JSONObject}



class TipsTestsSpecs extends WordSpec with Matchers with ScalatestRouteTest
  with TipsRoutes with JsonSupport with ScalaFutures {

  override implicit val timeout: Timeout = Timeout(5.seconds)
  override val tipsActor :  ActorRef = system.actorOf(TipsActors.props, "tipsActor")

  var id: String = "TESTID01"
  val userId: Int = 1
  val tipJson = ByteString(
    s"""{
          "id": "$id",
          "user_id": $userId,
          "amount": 20
        }
        """.stripMargin)


  "Tips API" should {

    "able to get all tips - GET /tips => return OK" in {
      Get("/tips") ~> tipsRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to get total of tips - GET /tips/total" in {
      Get("/tips/total") ~> tipsRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to create a Tip - POST /tips (json)" in {
      Post("/tips", HttpEntity(MediaTypes.`application/json`, tipJson)) ~> tipsRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to get a Tip - GET /tips/{id}" in {
      Get(s"/tips/$id") ~> tipsRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to get users whom give one or many tips - GET /tips/users => return OK" in {
      Get("/tips/users") ~> tipsRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to get the sum of tips by distinct users - GET /tips/users/distinct => return OK" in {
      Get("/tips/users/distinct") ~> tipsRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to get the total of tips by an unique user - GET /tips/users/{id}/total => return OK" in {
      Get(s"/tips/users/$userId/total") ~> tipsRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to delete a Tip - DELETE /tips/{id}" in {
      Delete(s"/tips/$id") ~> tipsRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }


  }
}