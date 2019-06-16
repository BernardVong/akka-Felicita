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
import com.felicita._utils.helpers.ActorsHelpers._
import com.felicita.actors.TipsActors._
import com.felicita.actors._
import spray.json.JsValue
import com.felicita._utils.helpers.RoutesHelpers._

import scala.concurrent.Future
import scala.util.{Failure, Success}


trait TipsRoutes {

  implicit val timeout: Timeout
  implicit lazy val tipsLogs: LoggingAdapter = Logging(system, classOf[TipsRoutes])
  implicit def system: ActorSystem

  def tipsActor: ActorRef

  lazy val tipsRoutes: Route =
  pathPrefix("tips") { concat(
    pathEnd { concat(
      get { onCompleteCustom(responseType = "tips", (tipsActor ? GetTips).mapTo[Tips]) },
      post { entity(as[JsValue]) { json => onCompleteCustom(responseType = "alert", (tipsActor ? CreateTip(json)).mapTo[Alert]) } }
    )},
    path("total") { get { onCompleteCustom(responseType = "alert", (tipsActor ? GetTotalTips).mapTo[Alert]) } },
    pathPrefix("users") { concat(
      pathEnd{ concat(
        get { onCompleteCustom(responseType = "users", (tipsActor ? GetTipsUsers).mapTo[Users]) },
      )},
      path("distinct") { get { onCompleteCustom(responseType = "tips-users", (tipsActor ? GetDistinctTipsUsers).mapTo[TipsUsers]) } },
      pathPrefix(Segment) { userId =>
        path("total") { get { onCompleteCustom(responseType = "alert", (tipsActor ? GetTotalTipsUser(userId.toInt)).mapTo[Alert]) } }
      }
    )},
    pathPrefix(Segment) { id => concat(
      get { onCompleteCustom(responseType = "tip", (tipsActor ? GetTip(id)).mapTo[Tip]) },
      delete { onCompleteCustom(responseType = "alert", (tipsActor ? DeleteTip(id)).mapTo[Alert]) },
    )}
  )}

}
