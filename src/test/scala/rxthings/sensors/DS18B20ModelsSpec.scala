package rxthings.sensors

import org.scalatest.{Matchers, WordSpec}
import rxthings.sensors.DS18B20ReadingModels.ValidDS18B20Reading
import spray.json._


class DS18B20ModelsSpec extends WordSpec with Matchers {
  import DS18B20ReadingProtocol._

  "valid reading" should {
    val o = ValidDS18B20Reading("foo", 123)
    val j = """{"device":"foo","value":123}"""

    "serialize" in {
      val json = o.toJson
      json shouldBe j.parseJson
    }
    
    "deserialize" in {
      val r = j.parseJson.convertTo[ValidDS18B20Reading]
      r shouldBe o
    }
  }
}
