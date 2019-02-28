package com.bunic
package introcats
package domain

import akka.actor.ActorSystem
import com.bunic.introcats.UnitSpec
import com.bunic.introcats.util.http.{ExceptionHandler, RejectionHandler}
import com.typesafe.scalalogging.LazyLogging
import org.mockito.Mockito._

import scala.concurrent.Future

class ShoutServiceSpec extends UnitSpec {

  "The Shout service" when {
    "initiated for getting last N tweets" should {
      "response with a 'Seq[Tweet]'" in {
        val ctx = new Context with StubData
        import ctx._

        val limit = 3
        val trimMock: Future[Seq[Tweet]] = Future(tweets(limit))
        when(trim.searchByUserName(userName, limit)).thenReturn(trimMock)

        svc.tweets(userName, limit).value.futureValue match {
          case Right(res) => res.size shouldEqual limit
          case Left(err) => failTest(err.message)
        }
      }

      "response with 10 tweets" in {
        val ctx = new Context with StubData
        import ctx._

        val limit = 10
        val trimMock: Future[Seq[Tweet]] = Future(tweets(limit))
        when(trim.searchByUserName(userName, limit)).thenReturn(trimMock)

        svc.tweets(userName, limit).value.futureValue match {
          case Right(res) => res.size shouldEqual limit
          case Left(err) => failTest(err.message)
        }
      }

      "response with an error stating the api is not available" in {
        val ctx = new Context with StubData
        import ctx._

        val limit = 10
        val trimMock: Future[Seq[Tweet]] = Future.failed(new Exception("Wrong credentials..."))
        when(trim.searchByUserName(userName, limit)).thenReturn(trimMock)

        svc.tweets(userName, limit).value.futureValue match {
          case Left(err) => err.message shouldEqual "Twitter api is not available"
          case Right(_) => failTest("Twitter connection error expected")
        }
      }

    }
  }

  trait Context extends LazyLogging with ExceptionHandler with RejectionHandler {
    import com.softwaremill.macwire._
    val actsys: ActorSystem = system

    val trim: infrastructure.TweetRepositoryInMemory = mock[infrastructure.TweetRepositoryInMemory]
    val svc: ShoutService = wire[ShoutService]
  }

}
