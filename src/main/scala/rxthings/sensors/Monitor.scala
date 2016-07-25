package rxthings.sensors

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.stream.ActorMaterializer
import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import rxthings.sensors.DS18B20ReadingModels.ValidDS18B20Reading
import rxthings.sensors.Monitor.Configure
import rxthings.sensors.SS1982a.{Channel, Close, Open}

object Monitor {
  def apply(relay: ActorRef, cfg: Config)(implicit sys: ActorSystem, mat: ActorMaterializer) = sys.actorOf(Props(new Monitor(relay, cfg)))

  case class Configure(config: Config)
}

// default cfg is passed in ctor, available if reset is needed due to invalid settings
class Monitor(relay: ActorRef, cfg: Config)(implicit sys: ActorSystem, mat: ActorMaterializer) extends Actor with ActorLogging {
  val history = History()


  override def preStart(): Unit = configure(cfg)


  def receive = unconfigured()


  def unconfigured(): Receive = {
    case Configure(config) => configure(config)
  }


  def configured(min: Int, over: Int): Receive = {
    case ScheduleReading(id, p) =>
      import context.dispatcher
      DS18B20.validReading(id, p).foreach(self ! _)

    case r: ValidDS18B20Reading => r.f match {
      case v if v < min => relay ! Close(Channel(1))
      case v if v >= min + over => relay ! Open(Channel(1))
    }
    case Configure(config) => configure(config)
  }


  def configure(config: Config) = {
    for (
      cfg <- config.getAs[Config]("temp");
      min <- cfg.getAs[Int]("min");
      over <- cfg.getAs[Int]("over")
    ) context.become(configured(min, over))
  }
}
