package com.felicita

//#quick-start-server
import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.concurrent.duration.Duration
import scala.util.{ Failure, Success }
import akka.http.scaladsl.server.Route
import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.actor.ActorSystem
import akka.Done
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import utils.SQLiteHelpers
import utils.FromMap.to

//#main-class
object WebServer extends App with SubscriberRoutes {

  // set up ActorSystem and other dependencies here
  //#main-class
  //#server-bootstrapping
  implicit val system: ActorSystem = ActorSystem("FelicitaAkkaHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  //#server-bootstrapping

  val subscriberRegistryActor: ActorRef = system.actorOf(SubscriberRegistryActor.props, "userRegistryActor")

//  case class Id(id: String)
//  case class First_name(first_name: String)
//  case class Last_name(last_name: String)
//  case class Pseudo(pseudo: String)
//
//  case class Keys(vec: Vector[Id])
//
//  implicit val keyFormat = jsonFormat1(Id)
//  implicit val keysFormat = jsonFormat1(Keys)
  //#main-class
  // from the UserRoutes trait
//  val url = s"""jdbc:sqlite:${args(0)}"""

  // lazy val routes: Route = userRoutes
//  val route: Route =
//      get {
//        pathPrefix("subscriber") {
//          val req = SQLiteHelpers.request(url, "SELECT * FROM table_test", Seq("key"))
//          req match {
//            case Some(r) => val values = r.flatMap(v => to[Key].from(v))
//              complete(values)
//            case None => complete("mauvaise table")
//          }
//        }
//      }
//  //#main-class
lazy val routes: Route = subscriberRoutes

  //#http-server
  val serverBinding: Future[Http.ServerBinding] = Http().bindAndHandle(routes, "localhost", 8080)

  serverBinding.onComplete {
    case Success(bound) =>
      println(s"Server online at http://${bound.localAddress.getHostString}:${bound.localAddress.getPort}/")
    case Failure(e) =>
      Console.err.println(s"Server could not start!")
      e.printStackTrace()
      system.terminate()
  }

  Await.result(system.whenTerminated, Duration.Inf)
  //#http-server
  //#main-class
}
//#main-class
//#quick-start-server
