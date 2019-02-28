package com.bunic
package introcats
package domain

import akka.http.scaladsl.model._
import io.circe.{Json, Printer}
import io.circe.syntax._

package object entities {

  import StatusCodes._
  import io.circe.generic.semiauto._
  import io.circe.{Decoder, Encoder}

  case class Error(code: StatusCode, message: String)

  object Error {
    implicit val encodeStatusCode: Encoder[StatusCode] =
      Encoder.encodeInt.contramap[StatusCode](_.intValue)

    implicit val decodeStatusCode: Decoder[StatusCode] =
      Decoder.decodeInt.emap ( int => try {
        Right(StatusCode.int2StatusCode(int))
      } catch {
        case _: RuntimeException => Left("StatusCode")
      })

    implicit val decoder: Decoder[Error] = deriveDecoder
    implicit val encoder: Encoder[Error] = deriveEncoder

//    private [domain] def apply(code: StatusCode, message: String): Error = new Error(code, message)
}

  val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)

  implicit class ErrorResponseWrapper(response: Error) {
    def toHttpResponse: HttpResponse = HttpResponse(
      response.code,
      entity = HttpEntity(ContentTypes.`application/json`, response.asJson.toString())
    )
  }

  implicit class HttpResponseWrapper(response: Json) {
    def toHttpResponse: HttpResponse = HttpResponse(
      OK,
      entity = HttpEntity(ContentTypes.`application/json`, printer.pretty(response))
    )
  }

}
