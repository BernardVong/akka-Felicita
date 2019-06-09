package com.felicita.actors

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.server.Directives.complete
import com.typesafe.config.ConfigFactory
import com.felicita.utils.SQLiteHelpers
import com.felicita.utils.FromMap.to

object GiveawayRegistryActor {

  final case class Giveaway(id: Int, description_giveaway: String)

  final case class Giveaways(Giveaway: Vector[Giveaway])

  final case object GetGiveaways

  final case class GetGiveaway(name: String)

  final case class GiveawayActionPerformed(description: String)

  def props: Props = Props[GiveawayRegistryActor]

  val giveaway_fields: Seq[String] = Seq("id", "description_giveaway")
  val url: String = ConfigFactory.load().getString("url")
  val table_name : String = "giveaways"

}

class GiveawayRegistryActor extends Actor with ActorLogging {

  import GiveawayRegistryActor._

  def receive: Receive = {
    case GetGiveaways => selectAll(table_name, giveaways => {
      sender() ! giveaways
    })
  }

  def selectAll(table_name: String, callback: Any => Any): Unit = {
    val query = "SELECT * FROM %s".format(table_name)
    val request = SQLiteHelpers.request(url, query, giveaway_fields)
    print(request)
    request match {
      case Some(r) => val values = r.flatMap(v => to[Giveaway].from(v))
        callback(Giveaways(values))
    }
  }
}

