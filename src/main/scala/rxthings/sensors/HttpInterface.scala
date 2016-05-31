package rxthings.sensors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.typesafe.config.ConfigFactory
import rxthings.sensors.DS18B20._
import rxthings.sensors.DS18B20ReadingProtocol._
import rxthings.webhooks.ActorWebApi

object HttpInterface {
  def apply(monitor: ActorRef)(implicit sys: ActorSystem) = sys.actorOf(Props(new HttpInterface(monitor)))
}

class HttpInterface(monitor: ActorRef) extends Actor with ActorWebApi {
  override def config = Option(ConfigFactory.load)

  override def preStart() = {
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
