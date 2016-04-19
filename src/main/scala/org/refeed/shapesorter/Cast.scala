package org.refeed.shapesorter

import scalaz.\/

import org.refeed.shapesorter.Cast.Result

trait Cast[+A] {

  def cast(value: Any): Cast.Result[A]

  /** Transform the casted value in case of success.
    *
    * Use it only with functions that cannot fail.
    */
  def map[B](f: A => B): Cast[B] = new MapCast(this, f)

  def flatMap[B](f: A => Cast[B]): Cast[B] = new FlatMapCast(this, f)

  /** Provides a cast that never fails as the result is reified */
  def reify: Cast[Result[A]] = new ReifiedCast[A](this)

  /** Inverse of [[reify]] */
  def unreify[B](implicit f: A <:< Result[B]): Cast[B] =
    new FlatMapCast[A, B](this, a => f(a).fold(
      l = ConstCast.failing,
      r = ConstCast.to
    ))
}

object Cast {
  type Result[+A] = Error \/ A

  case class Error(error: String, value: Any, context: Seq[String] = Seq.empty) {

    def message: String = s"${context.mkString(",")}: $error for value '$value'"

    def withContext(enclosingContext: String): Error = copy(context = enclosingContext +: context)
  }

  /** Lifts any function to a cast */
  def lift[A: Manifest, B](f: A => B): Cast[B] = Cast.to[A].map(f)

  def to[A](implicit ev: Manifest[A]): Cast[A] = new SimpleCast(ev)

  def toList[A: Manifest]: Cast[List[A]] = toList(Cast.to[A])

  def toList[A](elemCast: Cast[A]): Cast[List[A]] = new ListCast(elemCast)
}
