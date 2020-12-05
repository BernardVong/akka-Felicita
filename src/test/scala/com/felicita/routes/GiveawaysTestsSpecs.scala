package com.felicita.routes

import akka.actor.ActorRef
import akka.http.scaladsl.model.{HttpEntity, MediaTypes, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.{ByteString, Timeout}
import com.felicita._utils.JsonSupport
import com.felicita.actors._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest._
import scala.concurrent.duration._


class GiveawaysTestsSpecs extends WordSpec with Matchers with ScalatestRouteTest
  with GiveawaysRoutes with JsonSupport with ScalaFutures {

  override implicit val timeout: Timeout = Timeout(5.seconds)
  override val giveawaysActor :  ActorRef = system.actorOf(GiveawaysActors.props, "giveawaysActor")

  var id: Int = 99991
  var userId: Int = 1
  val giveawayJson = ByteString(
    s"""{
          "id": "$id",
          "name": "test_giveaway01",
          "user_id_winner": -1
        }
        """.stripMargin)

  val giveawaySubmitUser = ByteString(
    s"""{
          "user_id": $userId
        }
        """.stripMargin)


  "Giveaways API" should {

    "able to get all giveaways - GET /giveaways => return OK" in {
      Get("/giveaways") ~> giveawaysRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to create a Giveaway - POST /giveaways (json)" in {
      Post("/giveaways", HttpEntity(MediaTypes.`application/json`, giveawayJson)) ~> giveawaysRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to get a Giveaway - GET /giveaways/{id}" in {
      Get(s"/giveaways/$id") ~> giveawaysRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to get Entries by Giveaway - GET /giveaways/{id}/entries" in {
      Get(s"/giveaways/$id/entries") ~> giveawaysRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to create an Entry for a Giveaway - POST /giveaways/{id}/participate (json)" in {
      Post(s"/giveaways/$id/participate", HttpEntity(MediaTypes.`application/json`, giveawaySubmitUser)) ~> giveawaysRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to close a Giveaway - POST /giveaways/{id}/close (json)" in {
      Post(s"/giveaways/$id/close") ~> giveawaysRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to get results of a Giveaway - GET /giveaways/{id}/result" in {
      Get(s"/giveaways/$id/result") ~> giveawaysRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to delete a Giveaway - DELETE /giveaways/{id}" in {
      Delete(s"/giveaways/$id") ~> giveawaysRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }


  }
}