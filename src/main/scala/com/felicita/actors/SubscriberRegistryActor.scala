package com.felicita.actors

//#user-registry-actor
import com.typesafe.config.ConfigFactory
import scala.util.Properties
import akka.actor.{Actor, ActorLogging, Props}
import utils.SQLiteHelpers
import akka.Done
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import utils.SQLiteHelpers
import utils.Database
import utils.FromMap.to

object SubscriberRegistryActor {

  final case class Subscriber(id: String, first_name: String, last_name: String, pseudo: String)

  final case class Subscribers(subscribers: Vector[Subscriber])

  final case object GetSubscribers

  def props: Props = Props[SubscriberRegistryActor]
}

class SubscriberRegistryActor extends Actor with ActorLogging {

  import SubscriberRegistryActor._

  val database = new Database()

  def receive: Receive = {

    case GetSubscribers =>
      val url = database.envOrElseConfig("url")
      println(s"My secret value is $url")
      val req = SQLiteHelpers.request(url, "SELECT * FROM subscriber", Seq("id", "first_name", "last_name", "pseudo"))
      req match {
        case Some(r) => val values = r.flatMap(s => to[Subscriber].from(s))
          sender() ! Subscribers(values)
        case None => complete("mauvaise table")
      }
  }
}