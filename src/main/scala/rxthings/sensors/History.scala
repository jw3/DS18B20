package rxthings.sensors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import rxthings.sensors.DS18B20ReadingModels.{InvalidDS18B20Reading, ValidDS18B20Reading}


object History {
  def apply()(implicit sys: ActorSystem) = sys.actorOf(Props(new History()))
}

// is this a persistent actor?  probably
class History extends Actor with ActorLogging {

  def receive: Receive = {
    case ValidDS18B20Reading(dev, value) =>
      log.debug("history logged reading of {} on {}", value, dev)
    case InvalidDS18B20Reading(dev) =>
      log.error("history logged failed reading on {}", dev)
  }
}
