import sbt.url
import sbtpgp._


val versions = new {
  val scala = "2.12.11"
  val scalatest = "3.0.8"
  val mockito = "1.10.19"
  val mustache = "0.9.5"
  val xingyi = "0.5.6-SNAPSHOT"
}

lazy val commonSettings = Seq(
  version := "0.6.1-SNAPSHOT",
  organization := "one.xingyi",
  publishMavenStyle := true,
  scalaVersion := versions.scala,
  scalacOptions ++= Seq("-feature"),
  libraryDependencies += "org.mockito" % "mockito-all" % versions.mockito % "test",
  libraryDependencies += "org.scalatest" %% "scalatest" % versions.scalatest % "test",
  libraryDependencies += "one.xingyi" %% "core" % versions.xingyi,
  libraryDependencies += "one.xingyi" %% "core" % versions.xingyi % "test" classifier "tests"
)

lazy val publishSettings = commonSettings ++ Seq(
  pomIncludeRepository := { _ => false },
  publishMavenStyle := true,
  publishArtifact in Test := false,
  licenses := Seq("BSD-style" -> url("http://www.opensource.org/licenses/bsd-license.php")),
  homepage := Some(url("https://github.com/phil-rice/cdd")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/phil-rice/cdd"),
      "scm:git@github.com:phil-rice/cdd.git"
    )
  ),
  developers := List(
    Developer(
      id = "phil",
      name = "Philip Rice",
      email = "phil.rice@validoc.org",
      url = url("https://www.linkedin.com/in/phil-rice-53959460")
    )
  ),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  })


lazy val reflectionSettings = publishSettings ++ Seq(
  libraryDependencies += "org.scala-lang" % "scala-reflect" % versions.scala,
  libraryDependencies += "org.scala-lang" % "scala-compiler" % versions.scala
)

lazy val jsonSettings = Seq(
  libraryDependencies += "one.xingyi" %% "json4s" % versions.xingyi
)

lazy val mustacheSettings = publishSettings ++ Seq(
  libraryDependencies += "com.github.spullara.mustache.java" % "scala-extensions-2.11" % versions.mustache
)

lazy val scalatestSettings = publishSettings ++ Seq(
  libraryDependencies += "org.scalatest" %% "scalatest" % versions.scalatest
)

val cddmustache = (project in file("module/cddmustache")).
  settings(mustacheSettings)

val cddscalatest = (project in file("module/cddscalatest")).
  dependsOn(cddengine % "test->test;compile->compile").
  settings(scalatestSettings)


lazy val cddscenario = (project in file("module/cddscenario")).
  settings(reflectionSettings: _*)

val cddexamples = (project in file("module/cddexamples")).
  dependsOn(cddengine % "test->test;compile->compile").
  dependsOn(cddscalatest % "test->test;compile->compile").
  dependsOn(cddmustache % "test->test;compile->compile").
  settings(publishSettings)

lazy val cddengine = (project in file("module/cddengine")).
  settings(publishSettings: _*).
  dependsOn(cddscenario % "test->test;compile->compile").aggregate(cddscenario)

lazy val cddscripts = (project in file("module/cddscripts")).
  settings(publishSettings: _*)

lazy val cddTest = (project in file("module/cddTest")).
  settings(publishSettings: _*).
  settings(jsonSettings: _*).
  dependsOn(cddmustache % "test->test;compile->compile").aggregate(cddscenario)


val cdd = (project in file(".")).
  settings(publishSettings).
  settings(publishArtifact := false).
  aggregate(
    cddscenario, //
    cddengine, //
    cddmustache, //
    cddscalatest, //
    cddexamples, //
    cddscripts, //
    cddTest
  )