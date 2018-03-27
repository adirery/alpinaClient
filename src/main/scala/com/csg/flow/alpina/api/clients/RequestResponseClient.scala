package com.csg.flow.alpina.api.clients

import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.softwaremill.sttp.{SttpBackend, sttp, _}

import scala.concurrent.{ExecutionContext, Future}

object RequestResponseClient {

  def send()(implicit ec:ExecutionContext, akkaHttpBackend:SttpBackend[Future, Source[ByteString, Any]] )={

    for {
      response1 <- sttp.get(uri"http://localhost:8123/hello1").send()
      response2 <- sttp
        .post(uri"http://localhost:8124/hello2")
        .body(response1.unsafeBody)
        .send()
    } yield response2.unsafeBody

  }

}
