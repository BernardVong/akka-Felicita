package com.felicita.actors

import akka.actor.{Actor, ActorLogging, Props}

object SubscribersActors {
  final case class ActionPerformed(description: String)
  final case object GetSubscribers
  final case class CreateSubscriber(sub: Subscriber)
  final case class GetSubscriber(name: String)
  final case class DeleteSubscriber(name: String)

  def props: Props = Props[SubscribersActors]
}

case class Subscriber(number: Int, text: String)


class SubscribersActors extends Actor with ActorLogging  {
  import SubscribersActors._

  override def receive: Receive = {
    case GetSubscribers =>
      println("getSubs")
    case CreateSubscriber(sub) =>
      println("createSub")
    case GetSubscriber(name) =>
      println("getSub")
    case DeleteSubscriber(name) =>
      println("deleteSub")
  }
}

