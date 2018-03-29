import sbt._
import Keys._


val akkaVersion = "2.5.9"
val akkaHttpVersion = "10.0.11"
val sttpVersion = "1.1.6"
val circe = "0.8.0"

lazy val alpina = (project in file(".")).
  settings(
    name := "alpina",
    version := "0.0.1",
    organization := "com.csg.flow.alpina",
    scalaVersion := "2.11.8",
    mainClass in Compile := Some("com.csg.flow.alpina.api.Main")
  )
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

assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
  case "application.conf"                            => MergeStrategy.concat
  case "reference.conf"                              => MergeStrategy.discard
  case "posttradeutility.p12"                        => MergeStrategy.first
  case "unwanted.txt"                                => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
