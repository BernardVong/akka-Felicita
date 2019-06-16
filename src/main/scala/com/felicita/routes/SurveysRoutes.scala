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
import com.felicita.actors.SurveysActors._
import com.felicita.actors._
import spray.json.JsValue


trait SurveysRoutes {

  implicit val timeout: Timeout
  implicit lazy val surveysLogs: LoggingAdapter = Logging(system, classOf[SurveysRoutes])
  implicit def system: ActorSystem

  def surveysActor: ActorRef

  lazy val surveysRoutes: Route =
  pathPrefix("surveys") { concat(
    pathEnd { concat(
      get {onCompleteCustom(responseType = "surveys", (surveysActor ? GetSurveys).mapTo[Surveys]) },
      post { entity(as[JsValue]) { json => onCompleteCustom(responseType = "alert", (surveysActor ? CreateSurvey(json)).mapTo[Alert]) } }
    )},
    pathPrefix(Segment) { id => concat(
      path("result") { get{ onCompleteCustom(responseType = "alert", (surveysActor ? GetSurveyResult(id.toInt)).mapTo[Alert]) }},
      get { onCompleteCustom(responseType = "survey", (surveysActor ? GetSurvey(id.toInt)).mapTo[Survey]) },
      delete { onCompleteCustom(responseType = "alert", (surveysActor ? DeleteSurvey(id.toInt)).mapTo[Alert]) },
      post { entity(as[JsValue]) { json => onCompleteCustom(responseType = "alert", (surveysActor ? ResponseToSurvey(id.toInt, json)).mapTo[Alert]) } }
    )}
  )}
}
