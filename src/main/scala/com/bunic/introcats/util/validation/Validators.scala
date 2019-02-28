package com.bunic
package introcats
package util
package validation

import com.wix.accord.dsl.be
import com.wix.accord.transform.ValidationTransform.TransformedValidator
import com.wix.accord.dsl._

object Validators {

  implicit val limitValidator: TransformedValidator[Int] = com.wix.accord.dsl.validator[Int] { limit =>
    limit should be <= 10
  }

}
