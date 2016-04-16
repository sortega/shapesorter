package org.refeed.shapesorter

import scalaz.syntax.either._

import org.refeed.shapesorter.Cast.Result

class FlatMapCast[-A, +B](delegate: Cast[A], f: A => Cast[B]) extends Cast[B] {
  override def cast(value: Any): Result[B] =
    delegate.cast(value).fold(
      l = error => error.left,
      r = value => f(value).cast(value)
    )
}
