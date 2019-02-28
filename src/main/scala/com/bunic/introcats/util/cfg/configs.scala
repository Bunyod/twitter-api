package com.bunic
package introcats
package util
package cfg

/** The application configuration
  * @param http The Http settings of the current application
  * @param cache The Cache settings of the current application
  */
case class Config private[cfg] (
  http: HttpCfg,
  cache: CacheCfg
)

/** The Http configuration of the current application
  *
  * @param host The Host of the current application
  * @param port The Port to bind the current application to.
  */
case class HttpCfg private[cfg] (
  host: String,
  port: Int
)

/** The Cache configuration of the current application
  *
  * @param initcapacity The initial capacity of the cache.
  * @param maxcapacity The maximum capacity of the cache.
  * @param ttl (Time To Live) It is the number of seconds that an Element should live since it was created.
  * @param tti (Time To Idle) It is the number of seconds that an Element should live since it was last used.
  */
case class CacheCfg private[cfg] (
  initcapacity: Int,
  maxcapacity: Int,
  ttl: Int,
  tti: Int
)
