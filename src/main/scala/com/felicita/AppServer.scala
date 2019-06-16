package com.felicita

// Routes

import akka.actor.{ActorRef, ActorSystem}
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.{Failure, Success}
import com.felicita.actors._
import com.felicita.routes._



object AppServer extends App
  with UsersRoutes with TipsRoutes with GiveawaysRoutes with SurveysRoutes {

  override implicit lazy val timeout: Timeout = Timeout(5.seconds)

  implicit val system: ActorSystem = ActorSystem("felicita-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  /** ACTORS **/
  override val usersActor : ActorRef = system.actorOf(UsersActors.props, "usersActor")
  override val tipsActor : ActorRef = system.actorOf(TipsActors.props, "tipsActor")
  override val giveawaysActor : ActorRef = system.actorOf(GiveawaysActors.props, "giveawaysActor")
  override val surveysActor : ActorRef = system.actorOf(SurveysActors.props, "surveysActor")
  /** ACTORS END **/


  /** ROUTES **/
  object AppRouter {
    val routes: Route = usersRoutes ~ tipsRoutes ~ giveawaysRoutes ~ surveysRoutes
  }
  /** ROUTES END **/


  /** RUN SERVER **/
  val serverBinding: Future[Http.ServerBinding] = Http()(system).bindAndHandle(AppRouter.routes, "localhost", 8080)(materializer)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()
  serverBinding.flatMap(_.unbind()).onComplete(_ => system.terminate())
  /** RUN SERVER END**/

}