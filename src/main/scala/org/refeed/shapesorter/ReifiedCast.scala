package org.refeed.shapesorter

import org.refeed.shapesorter.Cast.Result
import scalaz.syntax.either._

class ReifiedCast[+A](delegate: Cast[A]) extends Cast[Result[A]] {
  override def cast(value: Any): Result[Result[A]] = delegate.cast(value).right
}
