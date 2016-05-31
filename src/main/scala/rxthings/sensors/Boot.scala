package rxthings.sensors

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import pigpio.scaladsl.GpioImplicits._
import pigpio.scaladsl._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}
import rxthings.sensors.DS18B20._

import scala.concurrent.duration.DurationInt


object Boot extends App with LazyLogging {
  val sysname = UUID.randomUUID.toString.take(7)
  implicit val system: ActorSystem = ActorSystem("ds18b20")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  DefaultInitializer.gpioInitialise() match {
    case Success(Init(lib, ver)) =>
      logger.debug("initialized pigpio V{}", ver.toString)
      logger.debug("starting application")
      run(lib)

    case Failure(ex) => system.terminate()
  }

  Await.ready(system.whenTerminated, Duration.Inf)
  DefaultInitializer.gpioTerminate()


  /**
   *
   */
  def run(implicit lgpio: PigpioLibrary) = {
    val relayPin = 3 // from config
    val relay = SS1982a(GpioPin(relayPin))

    val monitor = Monitor(relay)
    val http = HttpInterface(monitor)

    val id = "___from_config___"
    pathForId(id).map { dev =>
      import system.dispatcher
      system.scheduler.schedule(0.seconds, 10.seconds, monitor, validReading("", dev))
    }
  }
}
