package com.felicita.actors

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.server.Directives.complete
import com.typesafe.config.ConfigFactory
import com.felicita.utils.SQLiteHelpers
import com.felicita.utils.FromMap.to

object UserRegistryActor {

  final case class User(id: Int, first_name: String, last_name: String, pseudo: String, subscriber: Int, is_blacklist: Int)

  final case class Users(User: Vector[User])

  final case object GetUsers

  final case class GetUser(name: String)

  final case class SetBlacklist(pseudo: String)

  final case class UnsetBlacklist(pseudo: String)

  final case class ActionPerformedUser(description: String)

  val users_fields: Seq[String] = Seq("id", "first_name", "last_name", "pseudo", "subscriber", "is_blacklist")

  def props: Props = Props[UserRegistryActor]
}

class UserRegistryActor extends Actor with ActorLogging {

  import UserRegistryActor._

  val url: String = ConfigFactory.load().getString("url")
  println(s"My secret value is $url")
  val table_name = "users"

  def receive: Receive = {

    case GetUsers => selectAll(table_name, users => {
      sender() ! users
    })

    case GetUser(pseudo) => selectAll(table_name, users => {
      sender() ! users.asInstanceOf[Users].User.filter(_.pseudo == pseudo).head
    })

    case SetBlacklist(pseudo: String) =>
      """
        |methods : Patch
        |Path : users/{pseudo}/set-blacklist
      """.stripMargin
      val query = s"UPDATE users SET is_blacklist = 1 WHERE pseudo like '$pseudo'"
      SQLiteHelpers.request(url, query, Seq("pseudo"))
      sender() ! ActionPerformedUser(s"Users : ($pseudo) is blacklist")

    case UnsetBlacklist(pseudo: String) =>
      """
        |
        |Path : users/{pseudo}/unset-blacklist
      """.stripMargin
      val query = s"UPDATE users SET is_blacklist = 0 WHERE pseudo like '$pseudo'"
      SQLiteHelpers.request(url, query, Seq("pseudo"))
      sender() ! ActionPerformedUser(s"Users : ($pseudo) is not blacklist")
  }

  def selectAll(table_name: String, callback: Any => Any): Unit = {
    val query = "SELECT * FROM %s".format(table_name)
    val request = SQLiteHelpers.request(url, query, users_fields)
    request match {
      case Some(r) => val values = r.flatMap(v => to[User].from(v))
        callback(Users(values))
    }
  }
}



