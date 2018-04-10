package com.csg.flow.alpina.api

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.csg.flow.alpina.api.clients.ClientOrchestratorActor
import com.csg.flow.alpina.api.mock.backends.TestMetricsServers._
import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import com.csg.flow.alpina.api.mock.backends.TestServers._

import scala.concurrent.ExecutionContext
import com.csg.flow.alpina.api.clients.LatenciesClientFactory._
import com.csg.flow.alpina.api.clients.RawClientFactory._
import com.typesafe.config.ConfigFactory
import com.csg.flow.alpina.api.ssl.ConnectionContextFactory._

object Main extends App {
  // setup akka
  val config = ConfigFactory.load()
  implicit val actorSystem: ActorSystem = ActorSystem("alpina-client-system")
  implicit val ec:ExecutionContext = actorSystem.dispatcher
  implicit val mat:ActorMaterializer = ActorMaterializer()

  implicit val sttpBackend = AkkaHttpBackend
    .usingActorSystem(actorSystem, customHttpsContext = connectionContext(
      config.getBoolean("sttp.connection.isSecure")
    )
    )

  // start the servers
  if(config.getBoolean("sttp.connection.isMockBackend")){
    startServerStream()
    startMetricsServer()
  }

  actorSystem.actorOf(ClientOrchestratorActor.props(config))
  val numberOfRawClients = config.getInt("sttp.connection.raw.clients")

  rawStream(config, numberOfRawClients)

  val numberOfLatencyClients = config.getInt("sttp.connection.latencies.clients")

  latenciesStream(config, numberOfLatencyClients)


}
