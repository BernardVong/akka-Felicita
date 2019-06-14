package com.felicita.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{delete, get, post}
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._
import com.felicita.actors.UsersActors._
import com.felicita.actors._
import spray.json.JsValue
import com.felicita._utils.RoutesHelpers._


import scala.util.{Failure, Success}


trait UsersRoutes {

  implicit val timeout: Timeout
  implicit lazy val usersLogs: LoggingAdapter = Logging(system, classOf[UsersRoutes])
  implicit def system: ActorSystem

  def usersActor: ActorRef

  lazy val usersRoutes: Route =
  pathPrefix("users") {
    concat(
      pathEnd {
        concat(
          get { onCompleteCustom(responseType = "users", (usersActor ? GetUsers).mapTo[Users]) },
          post { entity(as[JsValue]) { json => onCompleteCustom(responseType = "alert", (usersActor ? CreateUser(json)).mapTo[Alert]) } }
        )
      },
      path("subscribers") {
        get { onCompleteCustom(responseType = "users", (usersActor ? GetSubscribers).mapTo[Users]) }
      },
      pathPrefix(Segment) { pseudo =>
        concat(
          get { onCompleteCustom(responseType = "user", (usersActor ? GetUser(pseudo)).mapTo[User]) },
          delete { onCompleteCustom(responseType = "alert", (usersActor ? DeleteUser(pseudo)).mapTo[Alert]) },
          path("blacklist") { patch { onCompleteCustom(responseType = "alert", (usersActor ? UpdateUserToggleField(pseudo, "is_blacklisted", toggleValue = true)).mapTo[Alert]) } },
          path("unblacklist") { patch { onCompleteCustom(responseType = "alert", (usersActor ? UpdateUserToggleField(pseudo, "is_blacklisted", toggleValue = false)).mapTo[Alert]) } },
          path("subscribe") { patch { onCompleteCustom(responseType = "alert", (usersActor ? UpdateUserToggleField(pseudo, "is_subscriber", toggleValue = true)).mapTo[Alert]) } },
          path("unsubscribe") { patch { onCompleteCustom(responseType = "alert", (usersActor ? UpdateUserToggleField(pseudo, "is_subscriber", toggleValue = false)).mapTo[Alert]) } },
        )
      }
    )
  }

}
