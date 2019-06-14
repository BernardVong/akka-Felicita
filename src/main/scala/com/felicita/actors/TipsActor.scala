package com.felicita.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.felicita._utils.{ActorsHelpers, SQLiteHelpers}
import com.typesafe.config.ConfigFactory
import com.felicita._utils.FromMap._
import com.felicita._utils.ActorsHelpers._
import spray.json.JsValue


/* tips classes */
final case class Tip(id: String, user_id: Int, amount: Double)
final case class Tips(tips: Vector[Tip])
final case class TipsUsers(tips: Map[String, Double])

/* tips classes */


object TipsActors {
  /* UTILS */
  final case class Alert(message: String)
  final case class AlertError(message: String = "", cause: Throwable = None.orNull) extends Exception(message, cause)

  /* TIPS */
  final case object GetTips
  final case class CreateTip(tip: JsValue)
  final case class GetTip(id: String)
  final case class DeleteTip(id: String)
  final case object GetTipsUsers
  final case object GetTotalTips
  final case class GetTotalTipsUser(userId: Int)
  final case object GetDistinctTipsUsers


  def props: Props = Props[TipsActors]

  val db_table = "tips"
  val db_path: String = ConfigFactory.load().getString("db_url")
  val db_fields: Seq[String] = Seq("user_id", "amount")
  val db_fields_with_id: Seq[String] = db_fields :+ "id"

}


class TipsActors extends Actor with ActorLogging  {
  import TipsActors._


  /** ROUTES **/
  override def receive: Receive = {

    case GetTips => sender() ! selectAll(db_table, db_fields_with_id)

    case CreateTip(request: JsValue) =>
      SQLiteHelpers.request(db_path, "INSERT INTO " +db_table+ " (" +getFieldsFromJsonToString(request)+ ") VALUES (" +getValuesFromJsonToString(request)+ ")", db_fields)
      sender() ! Alert("Tip added.")

    case GetTip(id) =>
      val tip = findById(db_table, db_fields_with_id, id)
      sender() ! (if(tip.isDefined) tip.get else akka.actor.Status.Failure(AlertError("Tip not found")))

    case DeleteTip(id) =>
      val tip = findById(db_table, db_fields_with_id, id).asInstanceOf[Option[Tip]]
      if(tip.isDefined) {
        val query = "DELETE FROM " +db_table+ " WHERE id='" + tip.get.id +"'"
        SQLiteHelpers.request(db_path, query, db_fields)
        sender() ! Alert("Tip removed.")
      }
      else sender() ! akka.actor.Status.Failure(AlertError("Tip not found"))

    case GetTipsUsers =>
      val users = selectAll("users", UsersActors.db_fields_with_id).asInstanceOf[Users]
      val donorsIDs = selectAll(db_table, db_fields_with_id).asInstanceOf[Tips].tips.map(_.user_id).distinct
      sender() ! Users(users.users.filter(user => donorsIDs.contains(user.id)))

    case GetTotalTips =>
      sender() ! Alert("Tips total: " + selectAll(db_table, db_fields_with_id).asInstanceOf[Tips].tips.map(_.amount).sum + " euros")

    case GetTotalTipsUser(userId) =>
      val message = selectAll(db_table, db_fields_with_id).asInstanceOf[Tips].tips.filter(_.user_id == userId).map(_.amount).sum + " euros"
      sender() ! Alert("Tips total for this user : " + message)

    case GetDistinctTipsUsers =>
      val users = selectAll("users", UsersActors.db_fields_with_id).asInstanceOf[Users]
      val tips = selectAll(db_table, db_fields_with_id).asInstanceOf[Tips].tips

      val tipsByUser = tips.groupBy(tip => tip.user_id)
        .map(user => (headOrEmpty(users.users.filter(_.id == user._1).map(_.pseudo)), user._2.map(_.amount).sum))

      sender() ! TipsUsers(tipsByUser)
  }
  /** ROUTES END **/


}

