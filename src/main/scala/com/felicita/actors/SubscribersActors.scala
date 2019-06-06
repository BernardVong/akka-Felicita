package com.felicita.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.felicita._utils.SQLiteHelpers
import com.typesafe.config.ConfigFactory
import com.felicita._utils.FromMap._

/* subscribers classes */
final case class Subscriber(id: String, first_name: String, last_name: String, pseudo: String)
final case class Subscribers(subscribers: Seq[Subscriber])
/* subscribers classes */




object SubscribersActors {
  final case class ActionPerformed(description: String)
  final case object GetSubscribers
  final case class CreateSubscriber(sub: Subscriber)
  final case class GetSubscriber(name: String)
  final case class DeleteSubscriber(name: String)

  def props: Props = Props[SubscribersActors]
}


class SubscribersActors extends Actor with ActorLogging  {
  import SubscribersActors._

  var subscribers = Set.empty[Subscriber]


  override def receive: Receive = {

    case GetSubscribers =>
      selectAll(subscribers => {
        sender() ! subscribers
      })

    case CreateSubscriber(subscriber: Subscriber) =>

      /** prepare query **/
      val queryParams = subscriber.getClass.getDeclaredFields.map(_.getName).mkString(",")
      val queryValues = subscriber.productIterator.to.mkString("'","','","'")
      val query = s"INSERT INTO subscribers ($queryParams) VALUES ($queryValues)"
      val url = ConfigFactory.load().getString("db_url")
      /** prepare query **/

      val request = SQLiteHelpers.request(url, query, Seq("first_name", "last_name", "pseudo"))
      println(request)
      // TODO : debug
      sender() ! ActionPerformed(s"Subscriber ${subscriber.pseudo} added.")


    case GetSubscriber(pseudo) =>
      selectAll(subscribers => {
        val subscriber = subscribers.asInstanceOf[Subscribers].subscribers.filter(_.pseudo == pseudo).head
        sender() ! subscriber
      })

    case DeleteSubscriber(pseudo) =>
      println("deleteSub")
  }


  /** UTILS **/
  def selectAll(callback: Any => Any): Unit =
  {
    val dbURL = ConfigFactory.load().getString("db_url")
    val query = "SELECT * FROM subscribers"
    val queryFields = Seq("id", "first_name", "last_name", "pseudo")

    val request = SQLiteHelpers.request(dbURL, query, queryFields)

    request match {
      case Some (r) => val values = r.flatMap (v => to[Subscriber].from (v) )
        callback(Subscribers(values))
    }
  }
  /** UTILS **/

}

