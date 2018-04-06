package com.csg.flow.alpina.api

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.csg.flow.alpina.api.mock.backends.TestMetricsServers._
import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import com.csg.flow.alpina.api.mock.backends.TestServers._
import scala.concurrent.ExecutionContext
import com.csg.flow.alpina.api.clients.StreamLatenciesClient._
//import com.csg.flow.alpina.api.clients.StreamClient._
import com.csg.flow.alpina.api.model.AssetServicingMessage
import com.typesafe.config.ConfigFactory
import com.csg.flow.alpina.api.ssl.ConnectionContextFactory._

object Main extends App {
  // setup akka
  val config = ConfigFactory.load()
  implicit val actorSystem: ActorSystem = ActorSystem("alpina-client-system")
  implicit val ec:ExecutionContext = actorSystem.dispatcher
  implicit val mat:ActorMaterializer = ActorMaterializer()

  val isSecure = config.getBoolean("sttp.connection.isSecure")

  val httpsContext = connectionContext(isSecure)

  implicit val sttpBackend = AkkaHttpBackend.usingActorSystem(actorSystem, customHttpsContext = httpsContext)

  // start the servers
  if(config.getBoolean("sttp.connection.isMockBackend")){
    startServerStream()
    startMetricsServer()
  }

  val protocol = config.getString("sttp.connection.protocol")
  val host = config.getString("sttp.connection.host")
  val metricsHost = config.getString("sttp.connection.metrics.host")


  stream[AssetServicingMessage](protocol, host, metricsHost)


}
