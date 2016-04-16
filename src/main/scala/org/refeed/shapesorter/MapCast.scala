package org.refeed.shapesorter
import org.refeed.shapesorter.Cast.Result

class MapCast[-A, +B](delegate: Cast[A], f: A => B) extends Cast[B] {
  override def cast(value: Any): Result[B] = delegate.cast(value).map(f)
}
