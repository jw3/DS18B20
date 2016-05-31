package rxthings.sensors

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}


object Monitor {
  def apply(relay: ActorRef)(implicit sys: ActorSystem) = sys.actorOf(Props(new Monitor(relay)))
}

class Monitor(relay: ActorRef)(implicit sys: ActorSystem) extends Actor with ActorLogging {
  val history = History()


  def receive: Receive = {
    case _ =>
  }
}
