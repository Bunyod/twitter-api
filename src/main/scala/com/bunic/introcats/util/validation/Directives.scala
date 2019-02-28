package com.bunic
package introcats
package util
package validation

import akka.http.scaladsl.server.Directives.{pass, reject}
import akka.http.scaladsl.server.{Directive, ValidationRejection}
import com.wix.accord.{Descriptions, Validator}

import scala.language.implicitConversions

trait Directives {
  def validate(magnet: ValidationMagnet): Directive[Unit] = magnet()
}

object Directives extends Directives

/**
  * @see <a href="http://spray.io/blog/2012-12-13-the-magnet-pattern">Magnet Pattern</a>
  */
sealed trait ValidationMagnet {
  def apply(): Directive[Unit]
}

object ValidationMagnet {
  implicit def fromObj[T](obj: T)(implicit validator: Validator[T]): ValidationMagnet =
    new ValidationMagnet {
      def apply(): Directive[Unit] = {
        val result = com.wix.accord.validate(obj)
        result match {
          case com.wix.accord.Success => pass
          case com.wix.accord.Failure(violations) => reject(
            ValidationRejection(violations map { v =>
              val path = Descriptions.render(v.path)
              if (v.constraint.contains("must fully match regular expression")) {
                s"'$path' is not valid"
              } else {
                s"$path: ${v.constraint}"
              }
            } mkString ", ")
          )
        }
      }
    }
}
