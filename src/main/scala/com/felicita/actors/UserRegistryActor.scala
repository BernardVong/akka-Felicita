package com.felicita.actors

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.server.Directives.complete
import utils.{Database, SQLiteHelpers}
import utils.FromMap.to

object UserRegistryActor {

  final case class User(id: Int, first_name: String, last_name: String, pseudo: String, subscriber: Int, is_blacklist: Int)

  final case class Users(User: Vector[User])

  final case object GetUsers

  case class SetBlacklist(pseudo: String)

  case class UnsetBlacklist(pseudo: String)

  final case class ActionPerformedUser(description: String)

  def props: Props = Props[UserRegistryActor]
}

class UserRegistryActor extends Actor with ActorLogging {

  import UserRegistryActor._

  val database = new Database()
  val url: String = database.envOrElseConfig("url")
  println(s"My secret value is $url")

  def receive: Receive = {

    case GetUsers =>
      """
        |methods : Get
        |Path : /users
      """.stripMargin
      println(s"My secret value is $url")
      val req = SQLiteHelpers.request(url, "SELECT * FROM users", Seq("id","first_name", "last_name", "pseudo", "subscriber", "is_blacklist"))
      req match {
        case Some(r) => val values = r.flatMap(s => to[User].from(s))
          sender() ! Users(values)
        case None => complete("mauvaise table")
      }
    case SetBlacklist(pseudo: String) =>
      """
        |methods : Patch
        |Path : users/{pseudo}/set-blacklist
      """.stripMargin
      val query = s"UPDATE users SET is_blacklist = 1 WHERE pseudo like '$pseudo'"
      SQLiteHelpers.request(url,query , Seq("pseudo"))

      sender() ! ActionPerformedUser(s"Users : ($pseudo) is blacklist")

    case UnsetBlacklist(pseudo: String) =>
      """
        |
        |Path : users/{pseudo}/unset-blacklist
      """.stripMargin
      val query = s"UPDATE users SET is_blacklist = 0 WHERE pseudo like '$pseudo'"
      SQLiteHelpers.request(url,query , Seq("pseudo"))
      sender() ! ActionPerformedUser(s"Users : ($pseudo) is not blacklist")
  }
}


