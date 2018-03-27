package com.csg.flow.alpina.api.clients

import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{RestartSource, Source}
import akka.util.ByteString
import com.csg.flow.alpina.api.marshal.AlpinaCirceSupport
import com.csg.flow.alpina.api.model.AssetServicingMessage
import com.softwaremill.sttp.{SttpBackend, asStream, sttp, _}
import de.knutwalker.akka.stream.JsonStreamParser

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

case class ApiMetrics(timestamp:String,
                      averageLatency:Double,
                      stdDeviationLatency:Double,
                      minLatency:Long,
                      maxLatency:Long,
                      numMessages:Long)

object StreamClient {

  def stream[T](endpoint:String)(implicit ec:ExecutionContext,
               akkaHttpBackend:SttpBackend[Future, Source[ByteString, Any]],
               as:ActorSystem) ={

    val ts_format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    implicit val mat: ActorMaterializer = ActorMaterializer()

    import AlpinaCirceSupport._

    RestartSource.withBackoff(
      minBackoff = 3.seconds,
      maxBackoff = 30.seconds,
      randomFactor = 0.2
    ){ () =>
      Source.fromFutureSource {
        sttp
          .body("<position>earliest</position>")
          .post(uri"$endpoint")
          .contentType("application/xml")
          .response(asStream[Source[ByteString, _]])
          .send()
          .flatMap(Unmarshal(_).to[Either[String, Source[ByteString, _]]])
          .map { resp =>
            resp match {
              case Right(messages) =>
                import io.circe._
                import io.circe.jawn.CirceSupportParser._
                messages
                  .via(JsonStreamParser.flow[Json])
                  //.map(decodeJson[AssetServicingMessage](_))
                  //.map{ m =>
                  //    (ts_format.parse(m.properties.timestamp).getTime - System.currentTimeMillis)/1000
                  //}
                  .groupedWithin(Int.MaxValue, 1000.millis)
                  //.map{latencies =>
                    //ApiMetrics(java.time.LocalDateTime.now().toString(), (latencies.reduce(_+_))/latencies.size, 0.0, latencies.min, latencies.max, latencies.size)
                  //}
                  .map(_.size)
                  .idleTimeout(45.seconds)
              case Left(error) =>
                println("An error has ocured: " + error)
                Source.single(0)
            }
          }
      }
    }.runForeach(println(_))
  }


}
