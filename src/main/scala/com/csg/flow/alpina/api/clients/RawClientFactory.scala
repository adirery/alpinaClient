package com.csg.flow.alpina.api.clients

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.csg.flow.alpina.api.Main.{actorSystem, config}
import com.csg.flow.alpina.api.clients.StreamClient._
import com.csg.flow.alpina.api.model.AssetServicingMessage
import com.csg.flow.alpina.api.ssl.ConnectionContextFactory.connectionContext
import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext


object RawClientFactory {

  def rawStream(config:Config, numberOfRawClients:Int=0)(implicit ec:ExecutionContext,actorSystem:ActorSystem, mat:ActorMaterializer) ={

    val protocol = config.getString("sttp.connection.protocol")
    val host = config.getString("sttp.connection.host")
    val metricsHost = config.getString("sttp.connection.metrics.host")

    implicit val sttpBackend = AkkaHttpBackend
      .usingActorSystem(actorSystem, customHttpsContext = connectionContext(
        config.getBoolean("sttp.connection.isSecure")
      )
      )

    for (i <- 0 to numberOfRawClients){
      stream[AssetServicingMessage](i, protocol, host, metricsHost)

    }

  }

}
