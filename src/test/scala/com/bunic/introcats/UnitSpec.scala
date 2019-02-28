package com.bunic
package introcats


import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directives, ExceptionHandler}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.scalalogging.LazyLogging
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar

abstract class UnitSpec extends AsyncWordSpec
  with Matchers with ScalatestRouteTest with MockitoSugar
  with ScalaFutures with LazyLogging {

  // Cf. https://stackoverflow.com/a/43128734/540718
  implicit def myExceptionHandler: ExceptionHandler =
    ExceptionHandler { case e =>
      val t = e.getStackTrace
      logger.error(s"Exception ${e.getMessage} (${e.getClass.getName} at ${t(0)})")
      Directives.complete(HttpResponse(StatusCodes.InternalServerError, entity = "Internal Server Error"))
    }


}
