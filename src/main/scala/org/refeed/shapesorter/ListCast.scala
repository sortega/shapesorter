package org.refeed.shapesorter

import java.util
import scala.collection.JavaConverters._
import scalaz._
import scalaz.syntax.either._
import scalaz.std.list.listInstance

import org.refeed.shapesorter.Cast.Result

class ListCast[+A](elemCast: Cast[A]) extends Cast[List[A]] {

  override def cast(value: Any): Result[List[A]] = value match {
    case elems: TraversableOnce[_] => castList(elems.toList)
    case elems: util.List[_] => castList(elems.asScala.toList)
    case _ => Cast.Error(s"not a sequence", value).left
  }

  private def castList(list: List[Any]): Result[List[A]] =
    Traverse[List].traverse[Result, Any, A](list.zipWithIndex) { case (elem, index) =>
      elemCast.cast(elem).leftMap(_.withContext(s"element at index $index"))
    }
}
