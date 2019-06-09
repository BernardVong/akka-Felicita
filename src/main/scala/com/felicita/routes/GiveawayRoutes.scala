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
import com.felicita.actors.GiveawayRegistryActor._
import com.felicita.supports.JsonSupport
import akka.util.Timeout
import akka.pattern.ask

trait GiveawayRoutes extends JsonSupport {
  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[GiveawayRoutes])

  def giveawayRegistryActor: ActorRef

  implicit lazy val timeout: Timeout = Timeout(5.seconds)

  lazy val giveawayRoutes: Route =
    pathPrefix("giveaways") {
      concat(
        pathEnd {
          concat(
            get {
              val giveaways: Future[Giveaways] =
                (giveawayRegistryActor ? GetGiveaways).mapTo[Giveaways]
              complete(giveaways)
            }
          )
        })
    }
}
