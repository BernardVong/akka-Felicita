package com.felicita.routes


import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
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
import com.felicita.actors.SubscribersActors._
import akka.util.Timeout

import scala.concurrent.Future
import akka.pattern.ask
import com.felicita._utils.JsonSupport
import com.felicita.actors._


trait SubscribersRoutes extends JsonSupport {

  implicit lazy val timeout = Timeout(5.seconds)
  implicit def system: ActorSystem
  def subscriberActor: ActorRef

  lazy val log = Logging(system, classOf[SubscribersRoutes])



  lazy val subscribersRoutes: Route =
  pathPrefix("subscribers") {
    concat(
      pathEnd {
        concat(
          get {
            val subscribers: Future[Subscribers] = (subscriberActor ? GetSubscribers).mapTo[Subscribers]
            complete(subscribers)
          },
          post {
            entity(as[Subscriber]) { subscriber =>
              val subscriberCreated: Future[ActionPerformed] = (subscriberActor ? CreateSubscriber(subscriber)).mapTo[ActionPerformed]
              onSuccess(subscriberCreated) { performed =>
                log.info("Added subsriber [{}]: {}", subscriber.pseudo, performed.description)
                complete((StatusCodes.Created, performed))
              }
            }
          }
        )
      },
      path(Segment) { pseudo =>
        concat(
          get {
            val subscriber: Future[Subscriber] = (subscriberActor ? GetSubscriber(pseudo)).mapTo[Subscriber]
            complete(subscriber)
          },
          delete {
            complete(200 -> "delete " + pseudo)
          }
        )
      }
    )
  }

}
