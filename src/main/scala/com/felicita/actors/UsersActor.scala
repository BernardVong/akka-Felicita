package com.felicita.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.felicita._utils.helpers._
import com.typesafe.config.ConfigFactory
import com.felicita._utils.FromMap._
import com.felicita._utils.helpers.ActorsHelpers._
import com.felicita._utils.helpers.SQLiteHelpers
import spray.json.JsValue



/* users classes */
final case class User(id: Int, pseudo: String, first_name: String, last_name: String, is_subscriber: Int, is_blacklisted: Int)
final case class Users(users: Seq[User])
/* users classes */


object UsersActors {
  /* USERS */
  final case object GetUsers
  final case class CreateUser(user: JsValue)
  final case class GetUser(name: String)
  final case class DeleteUser(name: String)

  /* SUBSCRIBERS & BLACKLIST */
  final case class UpdateUserToggleField(name: String, toggleField: String, toggleValue: Boolean)
  final case object GetSubscribers


  def props: Props = Props[UsersActors]

  val db_table = "users"
  val db_path: String = ConfigFactory.load().getString("db_url")
  val db_fields: Seq[String] = Seq("pseudo", "first_name", "last_name", "is_subscriber", "is_blacklisted")
  val db_fields_with_id: Seq[String] = db_fields :+ "id"
}


class UsersActors extends Actor with ActorLogging  {
  import UsersActors._


  /** ROUTES **/
  override def receive: Receive = {

    case GetUsers => sender() ! selectAll(db_table, db_fields_with_id).asInstanceOf[Users]

    case CreateUser(request: JsValue) =>
      val query = "INSERT INTO " +db_table+ " (" +getFieldsFromJsonToString(request)+ ") VALUES (" +getValuesFromJsonToString(request)+ ")"
      SQLiteHelpers.request(db_path, query, db_fields)
      sender() ! Alert("User added.")

    case GetUser(pseudo) =>
      val user = findByPseudo(db_table, db_fields_with_id, pseudo)
      sender() ! (if(user.isDefined) user.get else akka.actor.Status.Failure(AlertError("User not found")))

    case DeleteUser(pseudo) =>
      val user = findByPseudo(db_table, db_fields_with_id, pseudo)
      if(user.isDefined) {
        SQLiteHelpers.request(db_path, "DELETE FROM " +db_table+ " WHERE id=" + user.get.id, db_fields)
        sender() ! Alert("User removed.")
      }
      else sender() ! akka.actor.Status.Failure(AlertError("User not found"))

    case UpdateUserToggleField(pseudo, toggleField, toggleValue) =>
      val user = findByPseudo(db_table, db_fields_with_id, pseudo)
      if(user.isDefined) {
        val query = s"UPDATE $db_table SET $toggleField =  $toggleValue WHERE id=" + user.get.id
        SQLiteHelpers.request(db_path, query, db_fields)
        sender() ! Alert("User updated.")
      }
      else sender() ! akka.actor.Status.Failure(AlertError("User not found"))

    case GetSubscribers => sender() ! Users(selectAll(db_table, db_fields_with_id).asInstanceOf[Users].users.filter(_.is_subscriber == 1))

  }
  /** ROUTES END **/


  /** UTILS **/
  def findByPseudo(table: String, fields: Seq[String], pseudo: String): Option[User] = selectAll(table, fields).asInstanceOf[Users].users.find(_.pseudo == pseudo)
  /** UTILS END **/

}

