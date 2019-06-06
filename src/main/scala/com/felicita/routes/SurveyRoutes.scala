package com.felicita.routes

import akka.actor.{ ActorRef, ActorSystem }
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
import com.felicita.actors.SurveyRegistryActor._
import com.felicita.supports.JsonSupport
import akka.util.Timeout
import akka.pattern.ask

trait SurveyRoutes extends JsonSupport {

  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[SurveyRoutes])

  def surveyRegistryActor: ActorRef

  implicit lazy val timeout: Timeout = Timeout(5.seconds)

  lazy val surveyRoutes: Route =
    pathPrefix( "surveys") {
      concat(
        pathEnd {
          concat(
            get {
              val surveys: Future[Surveys] =
                (surveyRegistryActor ? GetSurveys).mapTo[Surveys]
              complete(surveys)
            },
            post {
              entity(as[Survey]) { survey =>
                val surveyCreated: Future[ActionPerformed] =
                  (surveyRegistryActor ? CreateSurvey(survey)).mapTo[ActionPerformed]
                onSuccess(surveyCreated) { performed =>
                  complete((StatusCodes.Created, performed))
                }
              }
            }
          )
        })
    }
}
