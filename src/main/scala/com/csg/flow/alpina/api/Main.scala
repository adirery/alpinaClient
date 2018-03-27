package com.csg.flow.alpina.api

import akka.actor.ActorSystem
import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import com.csg.flow.alpina.api.mock.backends.TestServers._
import scala.concurrent.ExecutionContext
import com.csg.flow.alpina.api.clients.StreamClient._
import com.csg.flow.alpina.api.model.AssetServicingMessage
import com.typesafe.config.ConfigFactory
import com.csg.flow.alpina.api.ssl.ConnectionContextFactory._

case class Tweet(uid: Int, txt: String)
case class Measurement(id: String, value: Int)


object Main extends App {
  // setup akka
  val config = ConfigFactory.load()
  implicit val actorSystem: ActorSystem = ActorSystem("alpina-system")
  implicit val ec:ExecutionContext = actorSystem.dispatcher

  val isSecure = config.getBoolean("sttp.connection.isSecure")

  val httpsContext = connectionContext(isSecure)

  implicit val sttpBackend = AkkaHttpBackend.usingActorSystem(actorSystem, customHttpsContext = httpsContext)

  // start the servers
  if(config.getBoolean("sttp.connection.isMockBackend")) startServerStream()

  stream[AssetServicingMessage]("https://aster.cspta.ch/custodian-event/plainsubscribe")

}
