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

object UserRegistryActor {

  final case class User(id: Int, first_name: String, last_name: String, pseudo: String, subscriber: Int, is_blacklist: Int)

  final case class Users(User: Vector[User])

  final case object GetUsers

  def props: Props = Props[UserRegistryActor]
}

class UserRegistryActor extends Actor with ActorLogging {

  import UserRegistryActor._

  val database = new Database()

  def receive: Receive = {

    case GetUsers =>
      val url = database.envOrElseConfig("url")
      println(s"My secret value is $url")
      val req = SQLiteHelpers.request(url, "SELECT * FROM users", Seq("id","first_name", "last_name", "pseudo", "subscriber", "is_blacklist"))
      req match {
        case Some(r) => val values = r.flatMap(s => to[User].from(s))
          sender() ! Users(values)
        case None => complete("mauvaise table")
      }
  }
}
