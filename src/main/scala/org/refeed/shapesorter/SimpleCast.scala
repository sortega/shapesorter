package org.refeed.shapesorter

import scalaz.syntax.either._

import org.refeed.shapesorter.Cast.Result

class SimpleCast[+A](manifest: Manifest[A]) extends Cast[A] {

  override def cast(value: Any): Result[A] = value match {

    case assignableValue if manifest.runtimeClass.isAssignableFrom(assignableValue.getClass) =>
      accept(assignableValue)

    case boxed: Integer if targetManifest[Int] => accept(boxed.intValue())
    case boxed: java.lang.Long if targetManifest[Long] => accept(boxed.longValue())
    case boxed: java.lang.Byte if targetManifest[Byte] => accept(boxed.byteValue())
    case boxed: java.lang.Float if targetManifest[Float] => accept(boxed.floatValue())
    case boxed: java.lang.Double if targetManifest[Double] => accept(boxed.doubleValue())
    case boxed: Character if targetManifest[Char] => accept(boxed.charValue())
    case boxed: java.lang.Boolean if targetManifest[Boolean] => accept(boxed.booleanValue())

    case _ =>
      val errorMessage = "cannot cast %s to %s".format(
        value.getClass.getCanonicalName,
        manifest.runtimeClass.getCanonicalName
      )
      Cast.Error(errorMessage, value).left
  }

  private def targetManifest[T](implicit targetManifest: Manifest[T]): Boolean =
    manifest.runtimeClass == targetManifest.runtimeClass

  private def accept(value: Any) = value.asInstanceOf[A].right
}
