package com.csg.flow.alpina.api.sink

import akka.actor.{Actor, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.csg.flow.alpina.api.clients.RequestResponseClient
import com.softwaremill.sttp.SttpBackend

import scala.concurrent.{ExecutionContext, Future}

case class Init()
case class Ack()
case class Complete()
case class ApiMetrics(name:String,
                      timestamp:Long,
                      averageLatency:Double,
                      stdDeviationLatency:Double,
                      minLatency:Long,
                      maxLatency:Long,
                      numMessages:Long,
                      numBytes: Long)

case class AvroApiMetrics(name:String, metric:Array[Byte])

class MetricsReporterActor(protocol:String, host:String)(implicit ec:ExecutionContext,
                           akkaHttpBackend:SttpBackend[Future, Source[ByteString, Any]],
                           mat: ActorMaterializer) extends Actor {

  override def receive: Receive = {

    case Complete =>
      println("MetricsReporterActor got complete sink message")

    case metrics: AvroApiMetrics =>
      RequestResponseClient.send(protocol, host, Seq(metrics))

    case metrics: Vector[AvroApiMetrics]=>
      RequestResponseClient.send(protocol, host, metrics)

    case _ => println("Unexpected message in MetricsReporterActor")
  }

}

object MetricsReporterActor{

  final val Name = "metrics-reporter-actor"

  def props(protocol:String, host:String)(implicit ec:ExecutionContext,
              akkaHttpBackend:SttpBackend[Future, Source[ByteString, Any]],
              mat: ActorMaterializer): Props
     = Props(new MetricsReporterActor(protocol, host))

}
