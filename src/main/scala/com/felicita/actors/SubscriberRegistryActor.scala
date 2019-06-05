package com.felicita

//#user-registry-actor
import akka.actor.{Actor, ActorLogging, Props}

//#user-case-classes
final case class Subscriber(id: String, first_name: String, last_name: String, pseudo: String)

final case class Subscribers(subscribers: Seq[Subscriber])

//#user-case-classes

object SubscriberRegistryActor {

  final case class ActionPerformed(description: String)

  final case object GetSubscribers

  def props: Props = Props[SubscriberRegistryActor]
}

class SubscriberRegistryActor extends Actor with ActorLogging {

  import SubscriberRegistryActor._

  var subscribers = Set.empty[Subscriber]

  def receive: Receive = {
    case GetSubscribers =>
      sender() ! Subscribers(subscribers.toSeq)
  }
}

//#user-registry-actor