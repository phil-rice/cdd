import sbt.url
import sbtpgp._


val versions = new {
  val scala12 = "2.12.12"
  val scala13 = "2.13.3"
  val scala = scala12
  val supportedScalaVersions = List(scala12, scala13)
  val scalatest = "3.2.2"
  val mockito = "1.10.19"
  val xingyi = "0.7.6"
}
lazy val normalCrossScala = Seq(crossScalaVersions := versions.supportedScalaVersions)

lazy val commonSettings = Seq(
  credentials += Credentials("Sonatype Nexus Repository Manager",
    "oss.sonatype.org",
    "phil.rice",
    "!WV9E4yYvNqD6jCr")

,
    version := versions.xingyi,
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
  libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value
)


lazy val scalatestSettings = publishSettings ++ Seq(
  libraryDependencies += "org.scalatest" %% "scalatest" % versions.scalatest
)
lazy val mustacheAndJson4sSettings = publishSettings ++ Seq(
  libraryDependencies += "one.xingyi" %% "json4s" % versions.xingyi,
  libraryDependencies += "one.xingyi" %% "mustache" % versions.xingyi
)


val cddscalatest = (project in file("module/cddscalatest")).
  dependsOn(cddengine % "test->test;compile->compile").
  settings(normalCrossScala).
  settings(scalatestSettings)

val cddtest = (project in file("module/cddtest")).
  dependsOn(cddengine % "test->test;compile->compile").
  settings(normalCrossScala).
  settings(mustacheAndJson4sSettings)

lazy val cddscenario = (project in file("module/cddscenario")).
  settings(normalCrossScala).
  settings(reflectionSettings: _*)

val cddexamples = (project in file("module/cddexamples")).
  dependsOn(cddengine % "test->test;compile->compile").
  dependsOn(cddscalatest % "test->test;compile->compile").
  settings(normalCrossScala).
  settings(mustacheAndJson4sSettings)

lazy val cddengine = (project in file("module/cddengine")).
  settings(publishSettings: _*).
  settings(normalCrossScala).
  dependsOn(cddscenario % "test->test;compile->compile").aggregate(cddscenario)

lazy val cddscripts = (project in file("module/cddscripts")).
  settings(normalCrossScala).
  settings(publishSettings: _*)

lazy val cddReq = (project in file("module/cddReq")).
  settings(normalCrossScala).
  settings(publishSettings: _*).
  dependsOn(cddscenario % "test->test;compile->compile").aggregate(cddscenario)




val cdd = (project in file(".")).
  settings(publishSettings).
  settings(publishArtifact := false, crossScalaVersions := Nil).
  aggregate(
    cddscenario, //
    cddengine, //
    cddscalatest, //
    cddexamples, //
    cddscripts, //
    cddReq,
    cddtest
  )
