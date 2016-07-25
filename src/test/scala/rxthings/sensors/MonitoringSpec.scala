package rxthings.sensors

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{AsyncWordSpecLike, Matchers, WordSpec, WordSpecLike}
import rxthings.sensors.DS18B20ReadingModels.ValidDS18B20Reading

import scala.concurrent.duration.{Duration, DurationInt}


class MonitoringSpec extends TestKit(ActorSystem("DS18B20Spec"))
                             with ImplicitSender with WordSpecLike with Matchers with DS18B20Tests {

  implicit val mat = ActorMaterializer()

  "scheduling readings" should {
    "work" in {
      val p = DS18B20.pathForId("good").get

      import system.dispatcher
      DS18B20.validReading("good", p).foreach(println)
      system.scheduler.schedule(0.seconds, 1.seconds, testActor, ScheduleReading("good", p))

      expectMsgPF(10.seconds) {
        case ValidDS18B20Reading =>
      }
    }
  }
}
