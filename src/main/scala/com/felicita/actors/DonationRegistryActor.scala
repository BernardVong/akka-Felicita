package com.felicita.actors

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.server.Directives.complete
import com.typesafe.config.ConfigFactory
import com.felicita.utils.SQLiteHelpers
import com.felicita.utils.FromMap.to

object DonationRegistryActor {

  final case class Donation(id: Int, user_id: String, amount: Double, description: String)

  final case class Donations(Donation: Vector[Donation])

  final case object GetDonations

  final case class GetDonation(name: String)

  final case class DonationActionPerformed(description: String)

  def props: Props = Props[DonationRegistryActor]

  val donation_fields: Seq[String] = Seq("id", "user_id", "amount", "description")
  val url: String = ConfigFactory.load().getString("url")
  val table_name : String = "donations"

}

class DonationRegistryActor extends Actor with ActorLogging {

  import DonationRegistryActor._

  def receive: Receive = {
    case GetDonations => selectAll(table_name, donations => {
      sender() ! donations
    })
  }

  def selectAll(table_name: String, callback: Any => Any): Unit = {
    val query = "SELECT * FROM %s".format(table_name)
    val request = SQLiteHelpers.request(url, query, donation_fields)
    print(request)
    request match {
      case Some(r) => val values = r.flatMap(v => to[Donation].from(v))
        callback(Donations(values))
    }
  }
}

