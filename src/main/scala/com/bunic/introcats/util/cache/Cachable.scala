package com.bunic
package introcats
package util
package cache

import akka.actor.ActorSystem
import akka.http.caching.scaladsl.{CachingSettings, LfuCacheSettings}
import com.bunic.introcats.util.cfg.Configurable
import scala.concurrent.duration.DurationInt

trait Cachable { self: Configurable =>

  def defaultCachingSettings(implicit actorSystem: ActorSystem): CachingSettings =
    CachingSettings(actorSystem)

  def lfuCacheSettings(implicit actorSystem: ActorSystem): LfuCacheSettings =
    defaultCachingSettings.lfuCacheSettings
      .withInitialCapacity(config.cache.initcapacity)
      .withMaxCapacity(config.cache.maxcapacity)
      .withTimeToLive(config.cache.ttl.seconds)
      .withTimeToIdle(config.cache.tti.seconds)



}
