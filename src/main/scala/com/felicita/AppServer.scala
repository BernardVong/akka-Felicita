package com.felicita

// Routes

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.felicita.actors.SubscribersActors
import com.felicita.routes.SubscribersRoutes

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.io.StdIn
import scala.util.{Failure, Success}


object AppServer extends App with SubscribersRoutes {

  implicit val system = ActorSystem("felicita-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val subscriberActor: ActorRef = system.actorOf(SubscribersActors.props, "subscriberActor")

  lazy val routes: Route = subscribersRoutes

  val serverBinding: Future[Http.ServerBinding] = Http()(system).bindAndHandle(routes, "localhost", 8080)(materializer)


  /** prompt message and bind stop server **/
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()
  serverBinding
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

}