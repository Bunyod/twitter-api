package com.bunic
package introcats
package domain

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

case class Tweet(text: String) {

  def transform: String = {
    val trimmed = text.trim

    // scalastyle:off
    val ignoreReplace = "[’?\"!\u2026]$".r // these are won't be replaced if matches at the end. \u2026 == ...
    val replaceWith = "[.,;:]$".r // these are will be replaced with exclamation mark at the end.
    // scalastyle:on

    val txt = if (trimmed.takeRight(3) != "..." && replaceWith.findFirstIn(trimmed).isDefined) {
      trimmed.dropRight(1) + "!"
    } else if (ignoreReplace.findFirstIn(trimmed).isDefined) {
      trimmed
    } else {
      trimmed + "!"
    }

    txt.toUpperCase
  }

}

object Tweet {

  implicit val decoder: Decoder[Tweet] = deriveDecoder
  implicit val encoder: Encoder[Tweet] = deriveEncoder

}
