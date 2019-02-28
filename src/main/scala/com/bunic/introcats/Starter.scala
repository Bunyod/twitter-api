package com.bunic
package introcats

import akka.actor.ActorSystem
import akka.http.caching.LfuCache
import akka.http.caching.scaladsl.{Cache, CachingSettings}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server.Directives.handleExceptions
import akka.http.scaladsl.server.directives.CachingDirectives.cache
import akka.http.scaladsl.server.{RequestContext, Route, RouteResult}
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import scala.concurrent.{ExecutionContext, Future}

import domain.ShoutService
import application.ShoutController

object Starter extends App
  with util.cfg.Configurable with util.cache.Cachable
  with util.http.ExceptionHandler with util.http.RejectionHandler
  with LazyLogging {

  def startApplication(): Future[Http.ServerBinding] = {

    implicit val actorSystem: ActorSystem = ActorSystem()
    implicit val executor: ExecutionContext = actorSystem.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer()(actorSystem)

    import com.softwaremill.macwire._

    val trim: infrastructure.TweetRepositoryInMemory = wire[infrastructure.TweetRepositoryInMemory]
    val svcShout: ShoutService = wire[ShoutService]
    val rteShout: ShoutController = wire[ShoutController]
    val shouts: Route = rteShout.route

    // Use the request's URI as the cache's key
    val keyerFunction: PartialFunction[RequestContext, Uri] = {
      case r: RequestContext => r.request.uri
    }
    val cachingSettings: CachingSettings = defaultCachingSettings.withLfuCacheSettings(lfuCacheSettings)
    val lfuCache: Cache[Uri, RouteResult] = LfuCache(cachingSettings)

    val routes: Route = handleExceptions(exceptionHandler) {
      cache(lfuCache, keyerFunction)(shouts)
    }

    Http().bindAndHandle(routes, config.http.host, config.http.port)
  }

  startApplication()

}
