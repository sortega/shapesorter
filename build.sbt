name := "shapesorter"

organization := "org.refeed"

javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

scalaVersion := "2.10.4"

crossScalaVersions := Seq("2.10.4", "2.11.4")

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.2.1",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)

bintraySettings

com.typesafe.sbt.SbtGit.versionWithGit
