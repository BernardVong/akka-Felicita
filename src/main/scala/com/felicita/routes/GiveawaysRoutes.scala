package com.felicita.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{delete, get, post}
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.pattern.ask
import akka.util.Timeout
import com.felicita._utils.helpers.ActorsHelpers.Alert
import com.felicita._utils.helpers.RoutesHelpers._
import com.felicita.actors.GiveawaysActors._
import com.felicita.actors._
import spray.json.{JsObject, JsValue}


trait GiveawaysRoutes {

  implicit val timeout: Timeout
  implicit lazy val giveawaysLogs: LoggingAdapter = Logging(system, classOf[GiveawaysRoutes])
  implicit def system: ActorSystem

  def giveawaysActor: ActorRef

  lazy val giveawaysRoutes: Route =
  pathPrefix("giveaways") { concat(
    pathEnd { concat(
      get { onCompleteCustom(responseType = "giveaways", (giveawaysActor ? GetGiveaways).mapTo[Giveaways]) },
      post { entity(as[JsValue]) { json => onCompleteCustom(responseType = "alert", (giveawaysActor ? CreateGiveaway(json)).mapTo[Alert]) } }
    )},
    pathPrefix(Segment) { id => concat(
      path("entries") { get { onCompleteCustom(responseType = "entries", (giveawaysActor ? GetEntries).mapTo[Entries]) }},
      path("participate") { post { entity(as[JsValue]) { json => onCompleteCustom(responseType = "alert", (giveawaysActor ? CreateEntry(id.toInt, json)).mapTo[Alert]) } }},
      path("close") { post { onCompleteCustom(responseType = "alert", (giveawaysActor ? closeGiveaway(id.toInt)).mapTo[Alert]) }},
      path("result") { get { onCompleteCustom(responseType = "alert", (giveawaysActor ? GetResult(id.toInt)).mapTo[Alert]) } },
      get { onCompleteCustom(responseType = "giveaway", (giveawaysActor ? GetGiveaway(id.toInt)).mapTo[Giveaway]) },
      delete { onCompleteCustom(responseType = "alert", (giveawaysActor ? DeleteGiveaway(id.toInt)).mapTo[Alert]) }
    )}
  )}
}
