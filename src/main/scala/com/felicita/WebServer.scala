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
import com.felicita.utils.SQLiteHelpers
import com.felicita.utils.FromMap.to
import com.felicita.routes.UserRoutes
import com.felicita.routes.SurveyRoutes
import com.felicita.routes.GiveawayRoutes
import com.felicita.routes.DonationRoutes
import com.felicita.actors.UserRegistryActor
import com.felicita.actors.SurveyRegistryActor
import com.felicita.actors.GiveawayRegistryActor
import com.felicita.actors.DonationRegistryActor
import scala.concurrent.duration._

object WebServer extends App with SurveyRoutes  with UserRoutes with GiveawayRoutes with DonationRoutes {

  override lazy val log = Logging(system, classOf)
  override implicit lazy val timeout: Timeout = Timeout(5.seconds)

  implicit val system: ActorSystem = ActorSystem("FelicitaAkkaHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  val surveyRegistryActor: ActorRef = system.actorOf(SurveyRegistryActor.props, "surveyRegistryActor")
  val userRegistryActor: ActorRef = system.actorOf(UserRegistryActor.props, "userRegistryActor")
  val giveawayRegistryActor: ActorRef = system.actorOf(GiveawayRegistryActor.props, "giveawayRegistryActor")
  val donationRegistryActor: ActorRef = system.actorOf(DonationRegistryActor.props, "donationRegistryActor")


  object MainRouter {
    lazy val routes: Route =  surveyRoutes ~ userRoutes ~ giveawayRoutes ~ donationRoutes
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

