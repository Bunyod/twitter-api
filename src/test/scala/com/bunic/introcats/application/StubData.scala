package com.bunic
package introcats
package application

import scala.util.Random

trait StubData {

  def randomString(): String = Random.alphanumeric.take(25).mkString("")

  val userName = randomString()
  val limit = 3
  val tweets = Seq(randomString(), randomString(), randomString())
}
