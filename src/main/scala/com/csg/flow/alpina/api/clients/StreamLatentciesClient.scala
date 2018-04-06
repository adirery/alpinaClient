package com.csg.flow.alpina.api.clients

import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{RestartSource, Sink, Source}
import com.csg.flow.alpina.api.marshal.{AlpinaCirceSupport, AvroSerializer}
import com.csg.flow.alpina.api.sink._
import akka.stream.scaladsl.{RestartSource, Source}
import akka.util.ByteString
import com.csg.flow.alpina.api.marshal.AlpinaCirceSupport
import com.softwaremill.sttp.{SttpBackend, asStream, sttp, _}
import de.knutwalker.akka.stream.JsonStreamParser

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import com.csg.flow.alpina.api.model.AssetServicingMessage



object StreamLatenciesClient {

  def stream[T](protocol:String, host:String, metricsHost:String)(implicit ec:ExecutionContext,
               akkaHttpBackend:SttpBackend[Future, Source[ByteString, Any]],
               as:ActorSystem) ={

    val ts_format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    implicit val mat: ActorMaterializer = ActorMaterializer()

    val metricsSink = Sink.actorRef(as.actorOf(MetricsReporterActor.props(protocol, metricsHost),
      MetricsReporterActor.Name), Complete)

    import AlpinaCirceSupport._
    import com.csg.flow.alpina.api.marshal.AvroSerializers._

    val serializer = new AvroSerializer[ApiMetrics]()
    val schemaId = 1

    import AlpinaCirceSupport._

    RestartSource.withBackoff(
      minBackoff = 3.seconds,
      maxBackoff = 30.seconds,
      randomFactor = 0.2
    ){ () =>
      Source.fromFutureSource {
        println(s"running latencies client")
        sttp
          .post(uri"$protocol://$host/custodian-event/subscribe")
          .auth.bearer("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiI4NjQ0NjRhMzEyZGU0MjFkYTBiMTdkOTIzNjRhNDYzNyIsIm5hbWUiOiJXaXBybyJ9.uSeKgrE4dElbt6UWv7ggVGc9WIMP3WAoaJnyGapMTOo")
          .body("<position>earliest</position>")
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
                  .via(JsonStreamParser.flow[Json].async)
                  .map(decodeJson[AssetServicingMessage](_)).async
                  .map { m => 
                      val l = (System.currentTimeMillis - ts_format.parse(m.properties.timestamp).getTime) / 1000
                      (m.payload.size+200, l)
                  }
                  .groupedWithin(Int.MaxValue, 1000.millis)
                  .map{bytesLatencies =>
                    val latencies = bytesLatencies.map(_._2)
                    AvroApiMetrics("kafka-endpoint", serializer.serialize(ApiMetrics(
                      "latencies-client",
                      System.currentTimeMillis,
                      (latencies.reduce(_ + _))/latencies.size,
                      5.0,
                      latencies.min,
                      latencies.max,
                      bytesLatencies.size,
                      bytesLatencies.map(_._1).reduce(_+_)), schemaId))
                  }
                  .groupedWithin(Int.MaxValue, 10.seconds)
                  .idleTimeout(90.seconds)
              case Left(error) =>
                println("An error has occured: " + error)
                Source.single(AvroApiMetrics("kafka-endpoint", serializer.serialize(ApiMetrics("",
                  System.currentTimeMillis,
                  0.0,
                  0.0,
                  0,
                  0,
                  0,
                  0), schemaId)
                ))
            }
          }
      }
    }.runWith(metricsSink)
  }
}
