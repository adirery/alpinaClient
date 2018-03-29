package com.csg.flow.alpina.api

import akka.actor.ActorSystem
<<<<<<< HEAD
import akka.stream.ActorMaterializer
import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import com.csg.flow.alpina.api.mock.backends.TestServers._
import com.csg.flow.alpina.api.mock.backends.TestMetricsServers._


=======
import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import com.csg.flow.alpina.api.mock.backends.TestServers._
>>>>>>> 794a6f5baa3872cf55cd0ab1741282ebb824e174
import scala.concurrent.ExecutionContext
import com.csg.flow.alpina.api.clients.StreamClient._
import com.csg.flow.alpina.api.model.AssetServicingMessage
import com.typesafe.config.ConfigFactory
import com.csg.flow.alpina.api.ssl.ConnectionContextFactory._

<<<<<<< HEAD
=======
case class Tweet(uid: Int, txt: String)
case class Measurement(id: String, value: Int)
>>>>>>> 794a6f5baa3872cf55cd0ab1741282ebb824e174


object Main extends App {
  // setup akka
  val config = ConfigFactory.load()
<<<<<<< HEAD
  implicit val actorSystem: ActorSystem = ActorSystem("alpina-client-system")
  implicit val ec:ExecutionContext = actorSystem.dispatcher
  implicit val mat:ActorMaterializer = ActorMaterializer()
=======
  implicit val actorSystem: ActorSystem = ActorSystem("alpina-system")
  implicit val ec:ExecutionContext = actorSystem.dispatcher
>>>>>>> 794a6f5baa3872cf55cd0ab1741282ebb824e174

  val isSecure = config.getBoolean("sttp.connection.isSecure")

  val httpsContext = connectionContext(isSecure)

  implicit val sttpBackend = AkkaHttpBackend.usingActorSystem(actorSystem, customHttpsContext = httpsContext)

  // start the servers
<<<<<<< HEAD
  if(config.getBoolean("sttp.connection.isMockBackend")){
    startServerStream()
    startMetricsServer()
  }

  val protocol = config.getString("sttp.connection.protocol")
  val host = config.getString("sttp.connection.host")


  stream[AssetServicingMessage](protocol, host)
=======
  if(config.getBoolean("sttp.connection.isMockBackend")) startServerStream()

  stream[AssetServicingMessage]("https://aster.cspta.ch/custodian-event/plainsubscribe")
>>>>>>> 794a6f5baa3872cf55cd0ab1741282ebb824e174

}
