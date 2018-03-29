import sbt._
import Keys._

val commonSettings = Seq(
  name := "alpina-client",
  organization := "com.csg.alpina",
  scalaVersion := "2.11.12",
  //scalafmtOnCompile := true,
  //scalafmtVersion := "1.4.0",
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xlint"),
  evictionWarningOptions in update := EvictionWarningOptions.default
    .withWarnTransitiveEvictions(false)
)

val akkaVersion = "2.5.9"
val akkaHttpVersion = "10.0.11"
val sttpVersion = "1.1.6"
val circe = "0.8.0"

lazy val rootProject = (project in file("."))
  .settings(commonSettings: _*)


lazy val alpina: Project = (project in file("alpina"))
  .settings(commonSettings: _*)
  .settings(
    name := "alpina",
    libraryDependencies ++= Seq(
      // an explicit dependency is needed to evict the transitive one from akka-http
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.softwaremill.sttp" %% "akka-http-backend" % sttpVersion,
      "com.softwaremill.sttp" %% "circe" % "1.1.12",
      "com.softwaremill.sttp" %% "brave-backend" % sttpVersion,
      "io.zipkin.reporter2" % "zipkin-sender-urlconnection" % "2.3.2",
      "de.knutwalker" %% "akka-stream-json" % "3.3.0",
      "de.knutwalker" %% "akka-stream-circe" % "3.4.0",
      "io.circe" %% "circe-generic" % circe,
      "org.apache.avro" % "avro" % "1.8.1"
    )
  )
