package rxthings.sensors

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import net.ceedubs.ficus.Ficus._
import pigpio.scaladsl.GpioImplicits._
import pigpio.scaladsl._
import rxthings.sensors.DS18B20._

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, DurationInt}
import scala.util.{Failure, Success}


object Boot extends App with LazyLogging {
  val sysname = UUID.randomUUID.toString.take(7)
  implicit val system: ActorSystem = ActorSystem("ds18b20")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  DefaultInitializer.gpioInitialise() match {
    case Success(Init(lib, ver)) =>
      logger.debug("initialized pigpio V{}", ver.toString)
      run(lib)

    case Failure(ex) =>
      logger.error("failed to initialize pigpio", ex)
      system.terminate()
  }

  Await.ready(system.whenTerminated, Duration.Inf)
  logger.debug("application terminating")
  DefaultInitializer.gpioTerminate()


  /**
   *
   */
  def run(implicit lgpio: PigpioLibrary) = {
    logger.debug("starting application")
    val config = system.settings.config.getConfig("furnace")

    val blower = config.as[Int]("pins.blower")
    logger.debug("assigning blower pin #{}", blower.toString)

    val blowerPin = GpioPin(blower)
    blowerPin ! OutputPin
    
    val relay = SS1982a(blowerPin)

    val monitor = Monitor(relay, config.getConfig("temp"))

    val http = HttpInterface(monitor)

    val tid = config.getString("thermostat")
    logger.debug("thermostat id [{}]", tid)

    pathForId(tid).map { p =>
      logger.debug("thermostat device [{}]", p)

      val interval = 10.seconds
      logger.debug("scheduling thermostat reading every {}", interval)

      import system.dispatcher
      system.scheduler.schedule(0.seconds, interval, monitor, ScheduleReading(tid, p))
    }
  }
}
