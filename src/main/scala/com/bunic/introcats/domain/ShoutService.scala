package com.bunic
package introcats
package domain

import akka.http.scaladsl.model.StatusCodes
import com.bunic.introcats.infrastructure.TweetRepositoryInMemory
import com.typesafe.scalalogging.LazyLogging
import entities.Error

import scala.concurrent.{ExecutionContext, Future}

/** ShoutService
  * Interacts with twitter api
  *
  * @param trim TweetRepositoryInMemory provides communication with Twitter
  */
class ShoutService(trim: TweetRepositoryInMemory)(implicit ec: ExecutionContext)
  extends LazyLogging {

  import cats.data._
  import cats.implicits._

  type Response[Success] = EitherT[Future, Error, Success]
  type TransformedTweet = String

  def tweets(username: String, limit: Int): Response[Seq[TransformedTweet]] = EitherT {
    trim.searchByUserName(username, limit)
      .map(_.map(_.transform).asRight[Error])
      .recover { case e: Throwable =>
        logger.error("Error occurred during loading tweets", e)
        Error(StatusCodes.InternalServerError, "Twitter api is not available").asLeft
      }
  }


}
