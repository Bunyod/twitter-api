package com.bunic
package introcats
package application

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives.{complete, get, path, _}
import akka.http.scaladsl.server.{Route, RouteConcatenation}
import scala.concurrent.ExecutionContext
import com.bunic.introcats.domain.ShoutService
import com.bunic.introcats.util.cfg.Configurable
import com.bunic.introcats.util.json.FailFastCirceSupport
import com.typesafe.scalalogging.LazyLogging
import io.circe.syntax._
import cats.implicits._

class ShoutController(svc: ShoutService)(implicit actsys: ActorSystem, ec: ExecutionContext)
  extends Configurable with FailFastCirceSupport with RouteConcatenation
  with LazyLogging {

  import domain.entities._

  val route: Route = get {
    path("shout" / Segment) { twitterUserName =>
      parameters('limit.as[Int]) { limit =>
        complete(
          svc.tweets(twitterUserName, limit).fold(
            err => err.toHttpResponse,
            res => res.asJson.toHttpResponse
          )
        )
      }
    }

  }

}
