package com.csg.flow.alpina.api.clients

import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.{ActorMaterializer, Materializer}
import com.csg.flow.alpina.api.clients.RawClientFactory._
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

case class StartIncreasing()
case class Schedule()

class ClientOrchestratorActor(config:Config)(implicit mat:ActorMaterializer, ec:ExecutionContext, actorSystem:ActorSystem) extends Actor {

  override def preStart(): Unit = {
    self ! Schedule
  }

  def receive()={
    case StartIncreasing =>
      println(s"starting client")
      rawStream(config, 1)

    case Schedule() =>
      context.system.scheduler.schedule(0 milliseconds, 1 minutes, self, StartIncreasing)

    case _ =>
      println("unknown message in ClientOrchestrator")

  }

}

object ClientOrchestratorActor{

  def props(config:Config)(implicit mat:ActorMaterializer, ec:ExecutionContext, actorSystem:ActorSystem):Props = Props(new ClientOrchestratorActor((config)))
}
