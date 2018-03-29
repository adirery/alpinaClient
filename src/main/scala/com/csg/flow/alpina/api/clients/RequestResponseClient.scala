package com.csg.flow.alpina.api.clients

<<<<<<< HEAD
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.csg.flow.alpina.api.marshal.AlpinaCirceSupport
import com.csg.flow.alpina.api.sink.{ApiMetrics, AvroApiMetrics}
import com.softwaremill.sttp.{SttpBackend, sttp, _}
import com.softwaremill.sttp.circe._
import io.circe.syntax._
=======
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.softwaremill.sttp.{SttpBackend, sttp, _}
>>>>>>> 794a6f5baa3872cf55cd0ab1741282ebb824e174

import scala.concurrent.{ExecutionContext, Future}

object RequestResponseClient {

<<<<<<< HEAD
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

=======
  def send()(implicit ec:ExecutionContext, akkaHttpBackend:SttpBackend[Future, Source[ByteString, Any]] )={

    for {
      response1 <- sttp.get(uri"http://localhost:8123/hello1").send()
      response2 <- sttp
        .post(uri"http://localhost:8124/hello2")
        .body(response1.unsafeBody)
        .send()
    } yield response2.unsafeBody
>>>>>>> 794a6f5baa3872cf55cd0ab1741282ebb824e174

  }

}
