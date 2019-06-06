package com.felicita.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{delete, get, post}
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import com.felicita._utils.JsonSupport
import com.felicita.actors.UsersActors._
import com.felicita.actors._

import scala.concurrent.Future
import scala.concurrent.duration._


trait UsersRoutes extends JsonSupport {

  implicit lazy val timeout = Timeout(5.seconds)
  implicit def system: ActorSystem
  def usersActor: ActorRef

  lazy val log = Logging(system, classOf[UsersRoutes])



  lazy val usersRoutes: Route =
  pathPrefix("users") {
    concat(
      pathEnd {
        concat(
          get {
            val users: Future[Users] = (usersActor ? GetUsers).mapTo[Users]
            complete(users)
          },
          post {
            entity(as[User]) { user =>
              val userCreated: Future[ActionPerformed] = (usersActor ? CreateUser(user)).mapTo[ActionPerformed]
              onSuccess(userCreated) { performed =>
                log.info("Added user [{}]: {}", user.pseudo, performed.description)
                complete((StatusCodes.Created, performed))
              }
            }
          }
        )
      },
      path(Segment) { pseudo =>
        concat(
          get {
            val user: Future[User] = (usersActor ? GetUser(pseudo)).mapTo[User]
            complete(user)
          },
          delete {
            complete(200 -> "delete " + pseudo)
          }
        )
      }
    )
  }

}
