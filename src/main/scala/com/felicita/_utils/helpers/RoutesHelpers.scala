package com.felicita._utils.helpers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.onComplete
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import com.felicita._utils.JsonSupport
import com.felicita._utils.helpers.ActorsHelpers.Alert
import com.felicita.actors._

import scala.concurrent.Future
import scala.util.{Failure, Success}

object RoutesHelpers extends JsonSupport {

  def onCompleteCustom(responseType: String, future: Future[Any]) : Route = {
    onComplete(future) {
      case Success (message) =>
        responseType match {
          case "alert"        => complete ((StatusCodes.OK, message.asInstanceOf[Alert]))
          case "user"         => complete ((StatusCodes.OK, message.asInstanceOf[User]))
          case "users"        => complete ((StatusCodes.OK, message.asInstanceOf[Users]))
          case "tip"          => complete ((StatusCodes.OK, message.asInstanceOf[Tip]))
          case "tips"         => complete ((StatusCodes.OK, message.asInstanceOf[Tips]))
          case "tips-users"   => complete ((StatusCodes.OK, message.asInstanceOf[TipsUsers]))
          case "giveaway"     => complete ((StatusCodes.OK, message.asInstanceOf[Giveaway]))
          case "giveaways"    => complete ((StatusCodes.OK, message.asInstanceOf[Giveaways]))
          case "entries"      => complete ((StatusCodes.OK, message.asInstanceOf[Entries]))
          case "survey"       => complete ((StatusCodes.OK, message.asInstanceOf[Survey]))
          case "surveys"      => complete ((StatusCodes.OK, message.asInstanceOf[Surveys]))

        }
      case Failure (exception) => complete ((StatusCodes.NotFound, s"An error occurred: ${exception.getMessage}") )
    }
  }

}
