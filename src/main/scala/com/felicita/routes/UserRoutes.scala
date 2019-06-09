package com.felicita.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging

import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.delete
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path

import scala.concurrent.Future
import com.felicita.actors.UserRegistryActor._
import com.felicita.supports.JsonSupport
import akka.util.Timeout
import akka.pattern.ask

trait UserRoutes extends JsonSupport {

  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[UserRoutes])

  def userRegistryActor: ActorRef

  implicit lazy val timeout: Timeout = Timeout(5.seconds)

  lazy val userRoutes: Route =
    pathPrefix("users") {
      concat(
        pathEnd {
          concat(
            get {
              val users: Future[Users] =
                (userRegistryActor ? GetUsers).mapTo[Users]
              complete(users)
            })
        },
        pathPrefix(Segment) { pseudo =>
          concat(
            path("set-blacklist") {
              patch {
                val user: Future[ActionPerformedUser] =
                  (userRegistryActor ? SetBlacklist(pseudo)).mapTo[ActionPerformedUser]
                complete(user)
              }
            },
            path("unset-blacklist") {
              patch {
                val user: Future[ActionPerformedUser] =
                  (userRegistryActor ? UnsetBlacklist(pseudo)).mapTo[ActionPerformedUser]
                complete(user)
              }
            },
            get {
              val user: Future[User] =
                (userRegistryActor ? GetUser(pseudo)).mapTo[User]
              complete(user)
            }
          )
        })
    }
}
