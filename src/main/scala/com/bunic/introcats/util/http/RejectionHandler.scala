package com.bunic
package introcats
package util
package http

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{RejectionHandler => AkkaRejectionHandler, _}
import akka.http.scaladsl.server.Directives._
import com.typesafe.scalalogging.LazyLogging
import domain.entities.Error
import io.circe.syntax._

/** Developer note:
 * Rejections are used by Akka HTTP to help us handle error scenarios more aptly.
 * When the filter directives do not let a request pass through their internal body
 * because the filtering conditions are not met, a rejection occurs. The result of rejection
 * is not in format `application/json` and not polished response message to the clients.
 * In order to be able to properly handle all kinds of error scenarios I use this `trait`.
 * Cf. https://doc.akka.io/docs/akka-http/current/routing-dsl/rejections.html
 */

trait RejectionHandler { self: LazyLogging =>
  implicit val rejectionHandler: AkkaRejectionHandler = AkkaRejectionHandler.newBuilder()
    .handle { case MissingQueryParamRejection(param) =>
      val errorResponse = Error(BadRequest, s"The required $param was not found.")
      complete(HttpResponse(
        BadRequest,
        entity = HttpEntity(ContentTypes.`application/json`, errorResponse.asJson.toString())
      ))
    }
    .handle { case param: MalformedQueryParamRejection =>
      val errorResponse = Error(BadRequest, s"The request was rejected because a $param could not be interpreted")
      complete(HttpResponse(
        BadRequest,
        entity = HttpEntity(ContentTypes.`application/json`, errorResponse.asJson.toString())
      ))
    }
    .handle { case ValidationRejection(message, _) =>
      val errorResponse = Error(BadRequest, message)
      complete(HttpResponse(
        BadRequest,
        entity = HttpEntity(ContentTypes.`application/json`, errorResponse.asJson.toString())
      ))
    }
    .handleNotFound {
      val errorResponse = Error(NotFound, "The requested resource could not be found.")
      complete(HttpResponse(
        NotFound,
        entity = HttpEntity(ContentTypes.`application/json`, errorResponse.asJson.toString())
      ))
    }
    .result()

}
