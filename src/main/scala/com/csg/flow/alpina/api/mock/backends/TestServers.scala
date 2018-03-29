package com.csg.flow.alpina.api.mock.backends

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.common.EntityStreamingSupport
import akka.http.scaladsl.server.Directives.{as, complete, entity, path, _}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}
import akka.util.ByteString
import com.csg.flow.alpina.api.model.{AssetServicingMessage, AssetServicingProperties}
import com.csg.flow.alpina.api.sink.{ApiMetrics, AvroApiMetrics}
import spray.json.JsValue

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Random

object TestMetricsServers extends akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
  with spray.json.DefaultJsonProtocol {

  implicit val actorSystem: ActorSystem = ActorSystem("alpina-metrics-test-servers")
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = actorSystem.dispatcher
  implicit val apiMetricsMessageFormat = jsonFormat2(AvroApiMetrics)
  implicit val jsonApiMetricsUnMarshaller = Unmarshaller[JsValue, AvroApiMetrics] { _ =>
    json => Future(json.convertTo[AvroApiMetrics])
  }

  def startMetricsServer(): Unit = {
    val routes: Route =
      path("metrics") {
        post {
          entity(as[Seq[AvroApiMetrics]]) { apiMetrics =>
            apiMetrics.foreach(println(_))
            complete("OK")
          }
        }
      }

    Await.result(Http().bindAndHandle(routes, "localhost", 8123), 1000.seconds)
  }
}

object TestServers {

  implicit val actorSystem: ActorSystem = ActorSystem("alpina-stream-test-servers")
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = actorSystem.dispatcher


  def startServerStream(): Unit = {
    val sep = ByteString("\n")

    implicit val jsonStreamingSupport = EntityStreamingSupport
      .json()
      .withFramingRenderer(Flow[ByteString].intersperse(sep))

    val routes: Route =
      path("subscribe") {
        post {
          complete {
            import com.csg.flow.alpina.api.marshal.AlpinaSprayJsonSupport._
            val payload = Random.alphanumeric.toString()
            Source.repeat[AssetServicingMessage](AssetServicingMessage(
              AssetServicingProperties("json",
                "CS",
                "03/26/2018 19:54:00",
                "signature",
                "MT564",
                "1.0",
                "FMP",
                "UP",
                0L),
              payload
            ))
          }
        }
      }

    Await.result(Http().bindAndHandle(routes, "localhost", 8125), 1000.seconds)
  }

}
