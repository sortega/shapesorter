package org.refeed.shapesorter

import scalaz.syntax.either._

import org.refeed.shapesorter.Cast.Result

case class ConstCast[+A](result: Result[A]) extends Cast[A] {
  override def cast(irrelevant: Any) = result
}

object ConstCast {
  def to[A](value: A): ConstCast[A] = ConstCast(value.right)
  def failing(error: Cast.Error): ConstCast[Nothing] = ConstCast(error.left)
}
