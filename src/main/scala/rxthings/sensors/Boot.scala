package rxthings.sensors

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.sun.org.apache.xml.internal.security.Init
import com.typesafe.scalalogging.LazyLogging
import net.ceedubs.ficus.Ficus._
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
    val config = system.settings.config.getConfig("furnace")

    val blower = config.as[Int]("pins.blower")
    val relay = SS1982a(GpioPin(blower))

    val monitor = Monitor(relay, config.getConfig("temp"))

    val http = HttpInterface(monitor)

    val tid = config.getString("thermostat")
    pathForId(tid).map { p =>
      import system.dispatcher
      system.scheduler.schedule(0.seconds, 10.seconds, monitor, validReading(tid, p))
    }
  }
}
