package com.bunic
package introcats


import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpRequest, StatusCodes}

class StarterIT extends IntegrationTest {

  "Service" should {
    "bind on port successfully and return 10 tweets" in {
      (for {
        serverBinding <- Starter.startApplication()
        result <- Http().singleRequest(HttpRequest(uri = "http://localhost:9000/shout/realDonaldTrump?limit=10"))
        _ <- serverBinding.unbind()
      } yield {
        result.status.intValue() shouldBe 200
        result.status shouldEqual StatusCodes.OK
      }).futureValue
    }

    "return with a ValidationRejection for invalid limit" in {
      (for {
        serverBinding <- Starter.startApplication()
        result <- Http().singleRequest(HttpRequest(uri = "http://localhost:9000/shout/realDonaldTrump?limit=12"))
        _ <- serverBinding.unbind()
      } yield {
        result.status.intValue() shouldBe 400
        result.status shouldEqual StatusCodes.BadRequest
      }).futureValue
    }

    "return cached result" in {
      (for {
        serverBinding <- Starter.startApplication()
        result1 <- Http().singleRequest(HttpRequest(uri = "http://localhost:9000/shout/realDonaldTrump?limit=2"))
        result2 <- Http().singleRequest(HttpRequest(uri = "http://localhost:9000/shout/realDonaldTrump?limit=2"))
        _ <- serverBinding.unbind()
      } yield {
        result1.entity shouldEqual result2.entity
      }).futureValue
    }

    "reset the cache and return different result" in {
      val requestWithHeader = HttpRequest(uri = "http://localhost:9000/shout/realDonaldTrump?limit=2")
      .withHeaders(RawHeader("Cache-Control", "no-cache"))

      (for {
        serverBinding <- Starter.startApplication()
        result1 <- Http().singleRequest(HttpRequest(uri = "http://localhost:9000/shout/realDonaldTrump?limit=2"))
        result2 <- Http().singleRequest(requestWithHeader)
        _ <- serverBinding.unbind()
      } yield {
        assert(result1.entity != result2.entity)
      }).futureValue
    }

  }

}
