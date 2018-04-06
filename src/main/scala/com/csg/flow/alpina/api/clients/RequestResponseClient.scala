package com.csg.flow.alpina.api.clients

import akka.stream.ActorMaterializer
import com.csg.flow.alpina.api.marshal.AlpinaCirceSupport
import com.csg.flow.alpina.api.sink.{ApiMetrics, AvroApiMetrics}
import com.softwaremill.sttp.circe._
import io.circe.syntax._
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.softwaremill.sttp.{SttpBackend, sttp, _}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object RequestResponseClient {

  def send(protocol:String, host:String, metrics:Seq[AvroApiMetrics])(implicit ec:ExecutionContext,
                                    akkaHttpBackend:SttpBackend[Future, Source[ByteString, Any]],
                                    mat: ActorMaterializer)={

    import AlpinaCirceSupport._

    val f = for {
      response <- sttp
        .post(uri"https://aster.cspta.ch/custodian-event/metrics")
        .contentType("application/json")
        .body(metrics.asJson)
        .send()
    } yield response.code

   f.onComplete{
      case Success(r) => //println(s" resp from metrics $r")
      case Failure(t) => //println(s" failed resp from metrics $t")

    }

  }

}
