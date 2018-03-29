package com.csg.flow.alpina.api.clients

import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.csg.flow.alpina.api.marshal.AlpinaCirceSupport
import com.csg.flow.alpina.api.sink.{ApiMetrics, AvroApiMetrics}
import com.softwaremill.sttp.{SttpBackend, sttp, _}
import com.softwaremill.sttp.circe._
import io.circe.syntax._

import scala.concurrent.{ExecutionContext, Future}

object RequestResponseClient {

  def send(protocol:String, host:String, metrics:Seq[AvroApiMetrics])(implicit ec:ExecutionContext,
                                    akkaHttpBackend:SttpBackend[Future, Source[ByteString, Any]],
                                    mat: ActorMaterializer)={

    import AlpinaCirceSupport._

    for {
      response <- sttp
        .post(uri"$protocol://$host:8123/metrics")
        .contentType("application/json")
        .body(metrics.asJson)
        .send()
    } yield response.code


  }

}
