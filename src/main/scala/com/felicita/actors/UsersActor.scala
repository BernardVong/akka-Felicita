package com.felicita.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.felicita._utils.SQLiteHelpers
import com.typesafe.config.ConfigFactory
import com.felicita._utils.FromMap._

/* users classes */
final case class User(id: Int, pseudo: String, first_name: String, last_name: String, is_subscriber: Boolean, is_blacklisted: Boolean)
final case class Users(users: Seq[User])
/* users classes */


object UsersActors {
  final case class ActionPerformed(description: String)
  final case object GetUsers
  final case class CreateUser(user: User)
  final case class GetUser(name: String)
  final case class DeleteUser(name: String)

  def props: Props = Props[UsersActors]

  val users_fields: Seq[String] = Seq("pseudo", "first_name", "last_name", "is_subscriber", "is_blacklisted")
  val users_db: String = ConfigFactory.load().getString("db_url")
}


class UsersActors extends Actor with ActorLogging  {
  import UsersActors._


  override def receive: Receive = {

    case GetUsers => selectAll(users => { sender() ! users })

    case CreateUser(user: User) =>
      val query = "INSERT INTO users (" +fieldsToString(user)+ ") VALUES (" +valuesToString(user)+ ")"
      val request = SQLiteHelpers.request(users_db, query, users_fields)
      sender() ! ActionPerformed(s"User ${user.pseudo} added.")

    case GetUser(pseudo) => selectAll(users => { sender() ! users.asInstanceOf[Users].users.filter(_.pseudo == pseudo).head })

    case DeleteUser(pseudo) =>
      println("deleteUser")
  }


  /** UTILS **/
  def fieldsToString(user: User) : String = user.getClass.getDeclaredFields.map(_.getName).mkString(",")
  def valuesToString(user: User) : String = user.productIterator.to.mkString("'","','","'")

  def selectAll(callback: Any => Any): Unit =
  {
    val dbURL = ConfigFactory.load().getString("db_url")
    val query = "SELECT * FROM users"

    val request = SQLiteHelpers.request(dbURL, query, users_fields)

    request match {
      case Some (r) => val values = r.flatMap (v => to[User].from (v) )
        callback(Users(values))
    }
  }
  /** UTILS END **/

}

