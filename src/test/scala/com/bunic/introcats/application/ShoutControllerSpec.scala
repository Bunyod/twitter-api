package com.bunic
package introcats
package application

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.server.{MissingQueryParamRejection, Route, ValidationRejection}
import akka.http.scaladsl.server.Directives._
import com.typesafe.scalalogging.LazyLogging
import domain.{entities, ShoutService}
import org.mockito.Mockito._
import scala.concurrent.Future

import cats.data._
import cats.implicits._

import util.http.{ExceptionHandler, RejectionHandler}

class ShoutControllerSpec extends UnitSpec {

  "The shout route" when {
    "called with 'GET /shout/'" should {
      "return with a HTTP status code of 200 and a JSON 'Seq[String]' response" in {
        val ctx = new Context with StubData
        import ctx._

        val mock: svc.Response[Seq[String]] = EitherT(
          Future(tweets.asRight[entities.Error])
        )
        when(svc.tweets(userName, limit)).thenReturn(mock)

        Get(s"/shout/$userName?limit=$limit") ~> rte ~> check {
          status.intValue() shouldBe 200
          status shouldEqual StatusCodes.OK
        }
      }

      "reject with a MissingQueryParamRejection for wrong field name" in {
        val ctx = new Context with StubData
        import ctx._

        Get(s"/shout/$userName?limi=$limit") ~> rte ~> check {
          rejection shouldBe an[MissingQueryParamRejection]
        }
      }

      "return with a ValidationRejection for invalid limit" in {
        val ctx = new Context with StubData
        import ctx._

        Get(s"/shout/$userName?limit=12") ~> rte ~> check {
          rejection shouldBe an[ValidationRejection]
        }
      }

      "return with a HTTP status code of 400 and a JSON 'Error' response" in {
        val ctx = new Context with StubData
        import ctx._

        val mock: svc.Response[Seq[String]] = EitherT(Future(
          entities.Error(StatusCodes.InternalServerError, "Error occurred during getting tweets").asLeft[Seq[String]]
        ))
        when(svc.tweets(userName, limit)).thenReturn(mock)

        Get(s"/shout/$userName?limit=$limit") ~> rte ~> check {
          status shouldEqual StatusCodes.InternalServerError
          contentType shouldEqual ContentTypes.`application/json`
        }
      }
    }
  }

  trait Context extends LazyLogging with ExceptionHandler with RejectionHandler {

    import com.softwaremill.macwire._

    val actsys: ActorSystem = system

    val svc: ShoutService = mock[ShoutService]
    val rte: Route = handleExceptions(exceptionHandler) {
      wire[ShoutController].route
    }
  }

}
