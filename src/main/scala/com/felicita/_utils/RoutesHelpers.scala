package com.felicita._utils

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.onComplete
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import com.felicita.actors.{Tip, Tips, TipsUsers, User, Users}
import com.felicita.actors.TipsActors.Alert
import com.felicita._utils.JsonSupport

import scala.concurrent.Future
import scala.util.{Failure, Success}

object RoutesHelpers extends JsonSupport {

  def onCompleteCustom(responseType: String, future: Future[Any]) : Route = {
    onComplete(future) {
      case Success (message) =>
        responseType match {
          case "user"         => complete ((StatusCodes.OK, message.asInstanceOf[User]))
          case "users"        => complete ((StatusCodes.OK, message.asInstanceOf[Users]))
          case "tip"          => complete ((StatusCodes.OK, message.asInstanceOf[Tip]))
          case "tips"         => complete ((StatusCodes.OK, message.asInstanceOf[Tips]))
          case "tips-users"   => complete ((StatusCodes.OK, message.asInstanceOf[TipsUsers]))
          case "alert"        => complete ((StatusCodes.OK, message.asInstanceOf[Alert]))
        }
      case Failure (exception) => complete ((StatusCodes.NotFound, s"An error occurred: ${exception.getMessage}") )
    }
  }

}
