package rxthings.sensors

import java.util.UUID

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{AsyncWordSpecLike, Matchers}
import rxthings.sensors.DS18B20ReadingModels.{InvalidDS18B20Reading, ValidDS18B20Reading}

import scala.concurrent.duration.DurationInt


class DS18B20Spec extends TestKit(ActorSystem("DS18B20Spec"))
                          with ImplicitSender with AsyncWordSpecLike with Matchers with DS18B20Tests {

  implicit val mat = ActorMaterializer()
  val timeout = 5 seconds
  val id = UUID.randomUUID.toString.take(5)

  "reading file" should {
    "handle good format" in {
      DS18B20Reading.fromFile(id, fdev("good")).runWith(Sink.head).map {
        case r: ValidDS18B20Reading =>
          r.device shouldBe id
          r.value shouldBe 12345
      }
    }

    "handle bad read" in {
      DS18B20Reading.fromFile(id, fdev("bad-reading")).runWith(Sink.head).map {
        case r: InvalidDS18B20Reading => r.device shouldBe id
      }
    }

    "handle bad format" in {
      DS18B20Reading.fromFile(id, fdev("bad-format")).runWith(Sink.head).map {
        case r: InvalidDS18B20Reading => r.device shouldBe id
      }
    }
  }
}
