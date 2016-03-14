package org.refeed.shapesorter

import scalaz.\/

trait Cast[+A] {

  def cast(value: Any): Cast.Result[A]

  /** Transform the casted value in case of success.
    *
    * Use it only with functions that cannot fail.
    */
  def map[B](f: A => B): Cast[B] = new TransformCast(this, f)
}

object Cast {
  type Result[+A] = Error \/ A

  case class Error(error: String, value: Any, context: Seq[String] = Seq.empty) {

    def message: String = s"${context.mkString(",")}: $error for value '$value'"

    def withContext(enclosingContext: String): Error = copy(context = enclosingContext +: context)
  }

  def to[A](implicit ev: Manifest[A]): Cast[A] = new SimpleCast(ev)

  def toList[A: Manifest]: Cast[List[A]] = new ListCast(Cast.to[A])
}
