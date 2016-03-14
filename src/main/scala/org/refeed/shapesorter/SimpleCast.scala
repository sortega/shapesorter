package org.refeed.shapesorter

import scalaz.syntax.either._

import org.refeed.shapesorter.Cast.Result

class SimpleCast[+A](manifest: Manifest[A]) extends Cast[A] {

  override def cast(value: Any): Result[A] = value match {

    case assignableValue: A if manifest.runtimeClass.isAssignableFrom(assignableValue.getClass) =>
      assignableValue.right

    case _ =>
      val errorMessage = "cannot cast %s to %s".format(
        value.getClass.getCanonicalName,
        manifest.runtimeClass.getCanonicalName
      )
      Cast.Error(errorMessage, value).left
  }
}
