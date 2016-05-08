package rxthings.sensors

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.Await
import scala.concurrent.duration.Duration


object Boot extends App {
  val sysname = UUID.randomUUID.toString.take(7)
  implicit val system: ActorSystem = ActorSystem("ds18b20")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val http = HttpInterface()

  Await.ready(system.whenTerminated, Duration.Inf)
}
