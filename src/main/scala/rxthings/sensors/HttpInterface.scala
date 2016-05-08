package rxthings.sensors

import java.nio.file.Path

import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import rxthings.sensors.DS18B20ReadingModels.ValidDS18B20Reading
import rxthings.sensors.DS18B20ReadingProtocol._
import rxthings.webhooks.ActorWebApi

object HttpInterface {
  def apply()(implicit sys: ActorSystem) = sys.actorOf(Props(new HttpInterface))

  def validReading(id: String, p: Path)(implicit sys: ActorSystem, mat: ActorMaterializer) = {
    import sys.dispatcher
    DS18B20Reading.fromFile(id, p).runWith(Sink.head).collect { case r: ValidDS18B20Reading => r }
  }
}

class HttpInterface extends Actor with ActorWebApi {
  import HttpInterface._

  override def preStart()= {
    webstart(routes)
  }

  def receive: Receive = {
    case _ =>
  }

  val routes =
    pathPrefix("read" / Segment) { dev =>
      get {
        DS18B20.pathForId(dev) match {
          case Some(p) => complete(validReading(dev, p))
          case None => complete(StatusCodes.BadRequest)
        }
      }
    }
}
