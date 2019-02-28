package com.bunic
package introcats
package util
package cfg

import com.typesafe.scalalogging.LazyLogging
import pureconfig._

trait Configurable {
  val config: Config = Configurable.config
}

object Configurable extends AnyRef with LazyLogging {

  val config: Config = loadConfig[Config] match {
    case Right(cfg) => logger.debug("Application configuration loaded."); cfg
    case Left(err) => sys.error("Cannot read config file:\n" + err.toList.mkString("\n"))
  }
}
