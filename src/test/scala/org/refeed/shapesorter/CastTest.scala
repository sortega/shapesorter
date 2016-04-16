package org.refeed.shapesorter

import scala.collection.JavaConverters._
import scalaz.syntax.either._

import org.refeed.shapesorter.Cast.Error
import org.scalatest.{FlatSpec, ShouldMatchers}

class CastTest extends FlatSpec with ShouldMatchers {

  val stringCast = Cast.to[String]
  val numberCast = Cast.to[Number]
  val stringsCast = Cast.toList[String]

  "Casting to a given class" should "succeed for values of the right class" in {
    stringCast.cast("hello") shouldBe "hello".right
  }

  it should "succeed for values of derived classes" in {
    numberCast.cast(12) shouldBe 12.right
  }

  it should "fail for values of unrelated classes" in {
    val invalid = Set.empty
    stringCast.cast(invalid) shouldBe
      Error("cannot cast scala.collection.immutable.Set.EmptySet$ to java.lang.String", invalid).left
  }

  "Casting to a seq of a given class" should "succeed for values of the right class" in {
    val someList = List("1", "2", "3")
    stringsCast.cast(someList) shouldBe someList.right
    stringsCast.cast(someList.toVector) shouldBe someList.right
    stringsCast.cast(for { i <- 1 to 3 } yield i.toString) shouldBe someList.right
  }

  it should "succeed for Java lists" in {
    val someList = List("hello", "world")
    stringsCast.cast(someList.asJava) shouldBe someList.right
  }

  it should "fail for non lists" in {
    val invalid = "not a list"
    stringsCast.cast(invalid) shouldBe Error("not a sequence", invalid).left
  }

  it should "fail for elements non conforming to the element type" in {
    stringsCast.cast(List("a", 2, "c")) shouldBe Error(
      error = "cannot cast java.lang.Integer to java.lang.String",
      value = 2,
      context = Seq("element at index 1")
    ).left
  }

  "Casting to a seq given an element cast" should "succeed if all the elements can de casted" in {
    val someList = List("1", "2", "3")
    val cast = Cast.toList(Cast.to[String])
    cast.cast(someList) shouldBe someList.right
  }

  "A cast" should "be mapped into a cast making some transformation" in {
    val symCast = stringCast.map(Symbol.apply)
    symCast.cast("symbol") shouldBe 'symbol.right
  }

  it should "be flat-mapped into a cast that depends on an intermediate value" in {
    val complexCast = stringCast.flatMap {
      case "null" => ConstCast.to(None)
      case _ => Cast.lift(Some.apply[String])
    }
    complexCast.cast("null") shouldBe None.right
    complexCast.cast("valid") shouldBe Some("valid").right
  }

  it should "be reify to access cast errors as values" in {
    val cast = stringCast.reify
    cast.cast("hello") shouldBe "hello".right.right
    cast.cast(1) shouldBe
      Error("cannot cast java.lang.Integer to java.lang.String", value = 1).left.right
  }

  "Any function" should "be lifted to a cast" in {
    val cast = Cast.lift((n: Integer) => n.toString)
    cast.cast(3) shouldBe "3".right
    cast.cast("hi") shouldBe 'left
  }
}
