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
import com.felicita.actors.DonationRegistryActor._
import com.felicita.supports.JsonSupport
import akka.util.Timeout
import akka.pattern.ask

trait DonationRoutes extends JsonSupport{
  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[DonationRoutes])

  def donationRegistryActor: ActorRef

  implicit lazy val timeout: Timeout = Timeout(5.seconds)

  lazy val donationRoutes: Route =
    pathPrefix("donations") {
      concat(
        pathEnd {
          concat(
            get {
              val donations: Future[Donations] =
                (donationRegistryActor ? GetDonations).mapTo[Donations]
              complete(donations)
            }
          )
        })
    }
}