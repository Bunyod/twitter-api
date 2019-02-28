package com.bunic
package introcats
package util
package http

import akka.http.scaladsl.model.IllegalRequestException
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{ExceptionHandler => AkkaExceptionHandler}
import akka.http.scaladsl.server.Directives._
import com.typesafe.scalalogging.LazyLogging
import domain.entities.Error

trait ExceptionHandler { self: LazyLogging =>
  val exceptionHandler = AkkaExceptionHandler {
    case ire: IllegalRequestException => extractUri { uri =>
      logger.error(s"Request to $uri could not be handled normally")
      complete(Error(BadRequest, s"${ire.getMessage}").toHttpResponse)
    }
    case iae: IllegalArgumentException => extractUri { uri =>
      logger.error(s"Request to $uri could not be handled normally")
      complete(Error(BadRequest, s"${iae.getMessage}").toHttpResponse)
    }
    case _: ArithmeticException => extractUri { uri =>
      logger.error(s"Request to $uri could not be handled normally")
      complete(Error(InternalServerError, "Arithmetic Exception Occured").toHttpResponse)
    }
    case e: Exception => extractUri { uri =>
      logger.error(s"Request to $uri could not be handled normally")
      logger.error(s"Exception during client request processing: ${e.getMessage}", e)
      complete(Error(InternalServerError, "Internal Server Error Occured").toHttpResponse)
    }
  }
}
