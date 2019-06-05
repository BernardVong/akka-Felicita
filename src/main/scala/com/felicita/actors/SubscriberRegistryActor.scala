package com.felicita

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
import utils.FromMap.to

//#user-case-classes
final case class Subscriber(id: String, first_name: String, last_name: String, pseudo: String)

final case class Subscribers(subscribers: Vector[Subscriber])

//#user-case-classes

object SubscriberRegistryActor {

  final case class ActionPerformed(description: String)

  final case object GetSubscribers

  def props: Props = Props[SubscriberRegistryActor]
}

class SubscriberRegistryActor extends Actor with ActorLogging {

  import SubscriberRegistryActor._

  var subscribers = Set.empty[Subscriber]

  val myConfig = new MyConfig()

  def receive: Receive = {

    case GetSubscribers =>
      val url = myConfig.envOrElseConfig("url")
      println(s"My secret value is $url")

      val req = SQLiteHelpers.request(url, "SELECT * FROM subscriber", Seq("id","first_name","last_name","pseudo"))
      println(req)
      req match {
        case Some(r) => val values = r.flatMap(s => to[Subscriber].from(s))
          sender() ! Subscribers(values)
        case None => complete("mauvaise table")
      }
      //sender() ! Subscribers(subscribers.toSeq)
  }
}

class MyConfig(fileNameOption: Option[String] = None) {

  val config = fileNameOption.fold(
    ifEmpty = ConfigFactory.load() )(
    file => ConfigFactory.load(file) )

  def envOrElseConfig(name: String): String = {
    Properties.envOrElse(
      name.toUpperCase.replaceAll("""\.""", "_"),
      config.getString(name)
    )
  }
}
//#user-registry-actor