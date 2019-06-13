package com.felicita.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.felicita._utils.{CaseClassHelpers, SQLiteHelpers}
import com.typesafe.config.ConfigFactory
import com.felicita._utils.FromMap._
import com.felicita._utils.CaseClassHelpers._
import spray.json.JsValue



/* users classes */
final case class User(id: Int, pseudo: String, first_name: String, last_name: String, is_subscriber: Int, is_blacklisted: Int)
final case class Users(users: Seq[User])
/* users classes */


object UsersActors {
  /* UTILS */
  final case class Alert(message: String)
  final case class AlertError(message: String = "", cause: Throwable = None.orNull) extends Exception(message, cause)

  /* USERS */
  final case object GetUsers
  final case class CreateUser(user: JsValue)
  final case class GetUser(name: String)
  final case class DeleteUser(name: String)

  /* SUBSCRIBERS & BLACKLIST */
  final case class UpdateUserToggleField(name: String, toggleField: String, toggleValue: Boolean)
  final case object GetSubscribers


  def props: Props = Props[UsersActors]


  val users_fields: Seq[String] = Seq("pseudo", "first_name", "last_name", "is_subscriber", "is_blacklisted")
  val users_fields_with_id: Seq[String] = users_fields :+ "id"
  val users_db: String = ConfigFactory.load().getString("db_url")
}


class UsersActors extends Actor with ActorLogging  {
  import UsersActors._


  /** ROUTES **/
  override def receive: Receive = {

    case GetUsers => sender() ! selectAll

    case CreateUser(request: JsValue) =>
      SQLiteHelpers.request(users_db, "INSERT INTO users (" +getFieldsFromJsonToString(request)+ ") VALUES (" +getValuesFromJsonToString(request)+ ")", users_fields)
      sender() ! Alert("User added.")

    case GetUser(pseudo) =>
      val user = selectByPseudo(pseudo)
      sender() ! (if(user.isDefined) user.get else akka.actor.Status.Failure(AlertError("User not found")))

    case DeleteUser(pseudo) =>
      val user = selectByPseudo(pseudo)
      if(user.isDefined) {
        SQLiteHelpers.request(users_db, "DELETE FROM users WHERE id=" + user.get.id, users_fields)
        sender() ! Alert("User removed.")
      }
      else sender() ! akka.actor.Status.Failure(AlertError("User not found"))

    case UpdateUserToggleField(pseudo, toggleField, toggleValue) =>
      val user = selectByPseudo(pseudo)
      if(user.isDefined) {
        val query = s"UPDATE users SET $toggleField =  $toggleValue WHERE id=" + user.get.id
        SQLiteHelpers.request(users_db, query, users_fields)
        sender() ! Alert("User updated.")
      }
      else sender() ! akka.actor.Status.Failure(AlertError("User not found"))

    case GetSubscribers => sender() ! Users(selectAll().users.filter(_.is_subscriber == 1))

  }
  /** ROUTES END **/


  /** UTILS **/
  def selectAll(): Users =
  {
    val request = SQLiteHelpers.request(ConfigFactory.load().getString("db_url"), "SELECT * FROM users", users_fields_with_id)

    request match {
      case Some (r) => val values = r.flatMap (v => to[User].from (v) )
        Users(values)
    }
  }

  def selectByPseudo(pseudo: String): Option[User] = selectAll().users.find(_.pseudo == pseudo)
  /** UTILS END **/

}

