package rxthings.sensors

import java.nio.file.{Files, Path, Paths}

import akka.stream.scaladsl._
import akka.util.ByteString
import rxthings.sensors.DS18B20ReadingModels.{InvalidDS18B20Reading, ValidDS18B20Reading}


trait DS18B20Reading {
  def device: String
}


object DS18B20Reading {
  val rhs = """t=(\d+)""".r.unanchored
  val flow = Flow[ByteString]
             .map(_.utf8String.split("""\r?\n"""))
             .map(s => parse(s.head, s.last))

  def fromFile(id: String, path: Path): Source[DS18B20Reading, _] = {
    FileIO.fromFile(path.toFile).via(flow).map {
      case Some(v) => ValidDS18B20Reading(id, v)
      case None => InvalidDS18B20Reading(id)
    }
  }

  def parse(source: (String, String)): Option[Int] = {
    source match {
      case (lhs, rhs(v)) if lhs.endsWith("YES") => Option(v.toInt)
      case _ => None
    }
  }
}


object DS18B20 {
  def pathForId(id: String): Option[Path] = {
    val pathstr = s"/sys/bus/w1/devices/$id/w1_slave"
    val devpath = Paths.get(pathstr)
    if (Files.exists(devpath)) Option(devpath) else None
  }
}
