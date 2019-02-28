package com.bunic
package introcats
package domain

import scala.concurrent.Future

trait TweetRepository {
  def searchByUserName(username: String, limit: Int): Future[Seq[Tweet]]
}
