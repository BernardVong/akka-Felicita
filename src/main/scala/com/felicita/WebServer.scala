package com.felicita

//#quick-start-server
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}
import akka.http.scaladsl.server.Route
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.actor.ActorSystem
import akka.Done
import akka.event.Logging
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.util.Timeout
import spray.json.DefaultJsonProtocol._
import utils.SQLiteHelpers
import utils.FromMap.to
import com.felicita.routes.SubscriberRoutes
import com.felicita.routes.SurveyRoutes
import com.felicita.actors.SubscriberRegistryActor
import com.felicita.actors.SurveyRegistryActor
import scala.concurrent.duration._

object WebServer extends App with SurveyRoutes  with SubscriberRoutes{

  override lazy val log = Logging(system, classOf)
  override implicit lazy val timeout: Timeout = Timeout(5.seconds)

  implicit val system: ActorSystem = ActorSystem("FelicitaAkkaHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  val surveyRegistryActor: ActorRef = system.actorOf(SurveyRegistryActor.props, "surveyRegistryActor")
  val subscriberRegistryActor: ActorRef = system.actorOf(SubscriberRegistryActor.props, "subscriberRegistryActor")

  object MainRouter {
    lazy val routes: Route =  surveyRoutes ~ subscriberRoutes
  }

  val serverBinding: Future[Http.ServerBinding] = Http().bindAndHandle(MainRouter.routes, "localhost", 8080)

  serverBinding.onComplete {
    case Success(bound) =>
      println(s"Server online at http://${bound.localAddress.getHostString}:${bound.localAddress.getPort}/")
    case Failure(e) =>
      Console.err.println(s"Server could not start!")
      e.printStackTrace()
      system.terminate()
  }

  Await.result(system.whenTerminated, Duration.Inf)
}
