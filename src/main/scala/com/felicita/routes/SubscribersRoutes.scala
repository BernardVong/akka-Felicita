package com.felicita.routes


import akka.http.scaladsl.server.Route

import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.delete
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path


trait SubscribersRoutes {

  lazy val subscribersRoutes: Route =
  pathPrefix("subscribers") {
    concat(
      pathEnd {
        concat(
          get {
            complete(StatusCodes.OK)
          },
          post {
            complete(StatusCodes.OK)
          }
        )
      },
      path(Segment) { pseudo =>
        concat(
          get {
            complete(200 -> "get " + pseudo)
          },
          delete {
            complete(200 -> "delete " + pseudo)
          }
        )
      }
    )
  }

}
