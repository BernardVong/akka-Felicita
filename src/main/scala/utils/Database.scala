package utils

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Properties

class Database(fileNameOption: Option[String] = None) {

  val config: Config = fileNameOption.fold(
    ifEmpty = ConfigFactory.load() )(
    file => ConfigFactory.load(file) )

  def envOrElseConfig(name: String): String = {
    Properties.envOrElse(
      name.toUpperCase.replaceAll("""\.""", "_"),
      config.getString(name)
    )
  }
}