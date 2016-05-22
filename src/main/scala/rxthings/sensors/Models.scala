package rxthings.sensors

import spray.json.{DefaultJsonProtocol, RootJsonFormat}


object DS18B20ReadingModels {
  case class ValidDS18B20Reading(device: String, value: Int) extends DS18B20Reading {
    val t = System.currentTimeMillis()
    lazy val c: Double = value / 1000
    lazy val f: Double = c * 1.8 + 32
  }


  case class InvalidDS18B20Reading(device: String) extends DS18B20Reading
}

object DS18B20ReadingProtocol extends DefaultJsonProtocol {
  import DS18B20ReadingModels._

  implicit val validFormat: RootJsonFormat[ValidDS18B20Reading] = jsonFormat(ValidDS18B20Reading, "device", "value")
  implicit val invalidFormat: RootJsonFormat[InvalidDS18B20Reading] = jsonFormat1(InvalidDS18B20Reading)
}
