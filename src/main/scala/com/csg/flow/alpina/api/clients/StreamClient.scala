package com.csg.flow.alpina.api.clients

import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
<<<<<<< HEAD
import akka.stream.scaladsl.{RestartSource, Sink, Source}
import akka.util.ByteString
import com.csg.flow.alpina.api.marshal.{AlpinaCirceSupport, AvroSerializer}
import com.csg.flow.alpina.api.model.AssetServicingMessage
import com.csg.flow.alpina.api.sink._
=======
import akka.stream.scaladsl.{RestartSource, Source}
import akka.util.ByteString
import com.csg.flow.alpina.api.marshal.AlpinaCirceSupport
import com.csg.flow.alpina.api.model.AssetServicingMessage
>>>>>>> 794a6f5baa3872cf55cd0ab1741282ebb824e174
import com.softwaremill.sttp.{SttpBackend, asStream, sttp, _}
import de.knutwalker.akka.stream.JsonStreamParser

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

<<<<<<< HEAD
object StreamClient {

  val ts_format = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss")


  def stream[T](protocol:String, host:String)(implicit ec:ExecutionContext,
               akkaHttpBackend:SttpBackend[Future, Source[ByteString, Any]],
               as:ActorSystem, mat: ActorMaterializer) ={


    val metricsSink = Sink.actorRef(as.actorOf(MetricsReporterActor.props(protocol, host),
      MetricsReporterActor.Name), Complete)


    import AlpinaCirceSupport._
    import com.csg.flow.alpina.api.marshal.AvroSerializers._

    val serializer = new AvroSerializer[ApiMetrics]()
    val schemaId = 0
=======
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
>>>>>>> 794a6f5baa3872cf55cd0ab1741282ebb824e174

    RestartSource.withBackoff(
      minBackoff = 3.seconds,
      maxBackoff = 30.seconds,
      randomFactor = 0.2
    ){ () =>
      Source.fromFutureSource {
        sttp
<<<<<<< HEAD
          .post(uri"$protocol://$host:8125/subscribe")
          .body("<position>latest</position>")
=======
          .body("<position>earliest</position>")
          .post(uri"$endpoint")
>>>>>>> 794a6f5baa3872cf55cd0ab1741282ebb824e174
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
<<<<<<< HEAD
                  //.via(JsonStreamParser.flow[Json])
                  //.map(decodeJson[AssetServicingMessage](_))
                  //.map { m =>
                    //(System.currentTimeMillis - ts_format.parse(m.properties.timestamp).getTime)/1000
                  //}
                  .groupedWithin(Int.MaxValue, 1000.millis)
                  .map{latencies =>
                    AvroApiMetrics("kafka-endpoint", serializer.serialize(ApiMetrics("",
                      System.currentTimeMillis,
                      0.0, //(latencies.reduce(_ + _))/latencies.size,
                      0.0,
                      0L,//latencies.min,
                      0L,//latencies.max,
                      latencies.size,
                      latencies.map(_.size).reduce(_ + _)), schemaId))
                  }
                  .groupedWithin(Int.MaxValue, 3.seconds)
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
=======
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


>>>>>>> 794a6f5baa3872cf55cd0ab1741282ebb824e174
}
