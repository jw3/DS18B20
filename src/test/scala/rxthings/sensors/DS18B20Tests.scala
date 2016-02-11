package rxthings.sensors

import java.nio.file.{Files, Paths}


trait DS18B20Tests {

  /**
   * produce path to file based device mock
   *
   * @param dev
   * @return
   */
  def fdev(dev: String) = {
    val path = s"/sys/bus/w1/devices/$dev/w1_slave"
    val r = getClass.getResource(path)
    val resource = Paths.get(r.getPath.drop(1))
    if (Files.exists(resource)) resource
    else throw new IllegalArgumentException(s"resource not found for $path")
  }
}
