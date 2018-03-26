package com.csg.flow.alpina.api.mock.backends

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.common.EntityStreamingSupport
import akka.http.scaladsl.server.Directives.{as, complete, entity, path, _}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}
import akka.util.ByteString
import com.csg.flow.alpina.api.model.{AssetServicingMessage, AssetServicingProperties}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

object TestServers {

  implicit val actorSystem: ActorSystem = ActorSystem("alpina-test-servers")
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec:ExecutionContext = actorSystem.dispatcher

  def startServer1(): Unit = {
    val routes: Route =
      path("hello1") {
        complete {
          "Hello 1!"
        }
      }

    Await.result(Http().bindAndHandle(routes, "localhost", 8123), 1000.seconds)
  }

  def startServer2(): Unit = {
    val routes: Route =
      path("hello2") {
        entity(as[String]) { body =>
          complete {
            body + " & Hello 2!"
          }
        }
      }

    Await.result(Http().bindAndHandle(routes, "localhost", 8124), 1000.seconds)
  }

  def startServerStream(): Unit = {
    val sep = ByteString("\n")

    implicit val jsonStreamingSupport = EntityStreamingSupport
      .json()
      .withFramingRenderer(Flow[ByteString].intersperse(sep))

    val routes: Route =
      path("subscribe") {
        complete {
          import com.csg.flow.alpina.api.marshal.AlpinaSprayJsonSupport._
          Source.repeat[AssetServicingMessage](AssetServicingMessage(
            AssetServicingProperties("", "", "", "", "","", "", "", 0L),
            ""

          ))
        }
      }

    Await.result(Http().bindAndHandle(routes, "localhost", 8125), 1000.seconds)
  }

}
