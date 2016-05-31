package rxthings.sensors

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import pigpio.scaladsl.Levels
import rxthings.sensors.SS1982a.{Channel, Close, Open}


object SS1982a {
  def apply(pins: ActorRef*)(implicit sys: ActorSystem) = sys.actorOf(Props(new SS1982a(pins.toList)))

  case class Channel(num: Int)
  case class Open(chan: Channel)
  case class Close(chan: Channel)
}

class SS1982a(pins: List[ActorRef]) extends Actor with ActorLogging {
  def receive: Receive = {
    case Open(Channel(num)) =>
      pins(num) ! Levels.high
    case Close(Channel(num)) =>
      pins(num) ! Levels.low
  }
}
