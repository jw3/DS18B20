package rxthings.sensors


case class ValidDS18B20Reading(device: String, value: Int) extends DS18B20Reading {
  val t = System.currentTimeMillis()
  lazy val c: Double = value / 1000
  lazy val f: Double = c * 1.8 + 32
}


case class InvalidDS18B20Reading(device: String) extends DS18B20Reading
