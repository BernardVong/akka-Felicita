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
import com.felicita.actors.UsersActors.CreateUser
import com.felicita.actors._
import com.felicita.routes._
import org.scalatest.concurrent.ScalaFutures
import spray.json._

import scala.concurrent.duration._
import scala.util.parsing.json.{JSON, JSONObject}



class UsersTestsSpecs extends WordSpec with Matchers with ScalatestRouteTest
  with UsersRoutes with JsonSupport with ScalaFutures {

  override implicit val timeout: Timeout = Timeout(5.seconds)
  override val usersActor :  ActorRef = system.actorOf(UsersActors.props, "usersActor")

  val pseudo = "mikao"
  val userJson = ByteString(
    s"""{
          "pseudo": "$pseudo",
          "first_name": "John",
          "last_name": "Wick",
          "is_subscriber": 0,
          "is_blacklisted": 0
        }
        """.stripMargin)


  "Users API" should {

    "able to get all users - GET /users => return OK" in {
      Get("/users") ~> usersRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to get all subscribers - GET /users/subscribers" in {
      Get("/users/subscribers") ~> usersRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to create an User - POST /users (json)" in {
      Post("/users", HttpEntity(MediaTypes.`application/json`, userJson)) ~> usersRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to get an User - GET /users/{pseudo}" in {
      Get(s"/users/$pseudo") ~> usersRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to blacklist an User - PATCH /users/{pseudo}/blacklist" in {
      Patch(s"/users/$pseudo/blacklist") ~> usersRoutes ~> check {
        val responseTest = response
        status should be(StatusCodes.OK)
      }
    }
    "able to unblacklist an User - PATCH /users/{pseudo}/unblacklist" in {
      Patch(s"/users/$pseudo/unblacklist") ~> usersRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "User can subscribe - PATCH /users/{pseudo}/subscribe" in {
      Patch(s"/users/$pseudo/subscribe") ~> usersRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "User can unsubscribe - PATCH /users/{pseudo}/unsubscribe" in {
      Patch(s"/users/$pseudo/unsubscribe") ~> usersRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
    "able to delete an User - DELETE /users/{pseudo}" in {
      Delete(s"/users/$pseudo") ~> usersRoutes ~> check {
        status should be(StatusCodes.OK)
      }
    }
  }
}