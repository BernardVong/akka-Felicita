package com.felicita.actors

import akka.actor.{Actor, ActorLogging, Props}
import com.felicita._utils.helpers.ActorsHelpers._
import com.felicita._utils.helpers.SQLiteHelpers
import com.typesafe.config.ConfigFactory
import spray.json.{JsObject, JsValue}


/* giveaways classes */
final case class Giveaway(id: Int, name: String, user_id_winner: Int)
final case class Giveaways(giveaways: Vector[Giveaway])

final case class Entry(id: Int, giveaway_id: Int, user_id: Int)
final case class Entries(entries: Vector[Entry])
/* giveaways classes */


object GiveawaysActors {

  /* GIVEAWAYS */
  final case object GetGiveaways
  final case class CreateGiveaway(giveaway: JsValue)
  final case class GetGiveaway(id: Int)
  final case class DeleteGiveaway(id: Int)

  /* ENTRIES */
  final case object GetEntries
  final case class CreateEntry(giveawayId: Int, userJson: JsValue)

  /* RESULTS */
  final case class closeGiveaway(giveawayId: Int) // random winner
  final case class GetResult(giveawayId: Int) // show winner


  def props: Props = Props[GiveawaysActors]

  val db_table = "giveaways"
  val db_path: String = ConfigFactory.load().getString("db_url")
  val db_fields: Seq[String] = Seq("name", "user_id_winner")
  val db_fields_with_id: Seq[String] = db_fields :+ "id"

  val db_entries = "giveaway_users"
  val db_entries_fields: Seq[String] = Seq("giveaway_id", "user_id")
  val db_entries_fields_with_id: Seq[String] = db_entries_fields :+ "id"

}


class GiveawaysActors extends Actor with ActorLogging  {
  import GiveawaysActors._

  /** ROUTES **/
  override def receive: Receive = {

    /** CRUD **/
    case GetGiveaways => sender() ! selectAll(db_table, db_fields_with_id)

    case CreateGiveaway(request: JsValue) =>
      SQLiteHelpers.request(db_path, "INSERT INTO " + db_table + " (" + getFieldsFromJsonToString(request) + ") VALUES (" + getValuesFromJsonToString(request) + ")", db_fields)
      sender() ! Alert("Giveaway added.")

    case GetGiveaway(id) =>
      val giveaway = findById(db_table, db_fields_with_id, id)
      sender() ! (if (giveaway.isDefined) giveaway.get else akka.actor.Status.Failure(AlertError("Giveaway not found")))

    case DeleteGiveaway(id) =>
      val giveaway = findById(db_table, db_fields_with_id, id).asInstanceOf[Option[Giveaway]]
      if (giveaway.isDefined) {
        val query = "DELETE FROM " + db_table + " WHERE id='" + giveaway.get.id + "'"
        SQLiteHelpers.request(db_path, query, db_fields)
        sender() ! Alert("Giveaway removed.")
      }
      else sender() ! akka.actor.Status.Failure(AlertError("Giveaway not found"))

    /** CRUD END **/
    case GetEntries => sender() ! selectAll(db_entries, db_entries_fields_with_id)

    case CreateEntry(giveawayId, userJson) =>
      val giveaway = findById(db_table, db_fields_with_id, giveawayId).asInstanceOf[Option[Giveaway]]
      val userId = userJson.asInstanceOf[JsObject].getFields("user_id").head.toString().toInt
      val user = findById(UsersActors.db_table, UsersActors.db_fields_with_id, userId).asInstanceOf[Option[User]]

      if(giveaway.isDefined && user.isDefined)
        user match {
          case Some(values) =>
            if(values.is_blacklisted == 0) {
              SQLiteHelpers.request(db_path, s"INSERT INTO $db_entries (giveaway_id, user_id) VALUES ('$giveawayId', $userId)", db_entries_fields_with_id)
              sender() ! Alert("Your participation is submit, thank you !")
            }

            else sender() ! akka.actor.Status.Failure(AlertError("User blacklisted can't submit to a giveaway."))
        }
      else if(giveaway.isEmpty) sender() ! akka.actor.Status.Failure(AlertError("Giveaway not found"))
      else if(user.isEmpty) sender() ! akka.actor.Status.Failure(AlertError("User not found"))


    case closeGiveaway(giveawayId) =>
      val giveaway = findById(db_table, db_fields_with_id, giveawayId).asInstanceOf[Option[Giveaway]]
      if (giveaway.isDefined) {
        val entries = selectAll(db_entries, db_entries_fields_with_id).asInstanceOf[Entries].entries.filter(_.giveaway_id == giveawayId).map(_.user_id).distinct
        val entriesWithTipsFactor = entries.flatMap(userId => Seq.fill(1+1*SumTipsByUser(userId).toInt)(userId))
        val random = new scala.util.Random
        val winner = entriesWithTipsFactor(random.nextInt(entriesWithTipsFactor.size + 1))

        SQLiteHelpers.request(db_path, s"UPDATE $db_table SET user_id_winner =  $winner WHERE id=" + giveaway.get.id, db_fields)
        sender() ! Alert(s"Giveaway winner is the user ID $winner.")
      }
      else sender() ! akka.actor.Status.Failure(AlertError("Giveaway not found"))

    case GetResult(giveawayId) =>
      findById(db_table, db_fields_with_id, giveawayId) match {
        case None => sender() ! akka.actor.Status.Failure(AlertError("Giveaway not found"))
        case Some(value) => sender() ! Alert("Giveaway winner is the user ID " + value.asInstanceOf[Giveaway].user_id_winner)
      }

  }
  /** ROUTES END **/


}

