package com.csg.flow.alpina.api.clients

import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.{ActorMaterializer, Materializer}
import com.csg.flow.alpina.api.clients.RawClientFactory._
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

case class Schedule(name:Int)
case class StartIncreasing(name:Int)


class ClientOrchestratorActor(config:Config)(implicit mat:ActorMaterializer, ec:ExecutionContext, actorSystem:ActorSystem) extends Actor {


  override def preStart(): Unit = {
    self ! Schedule(0)
  }

  def receive()={
    case StartIncreasing(name) =>
      println(s"starting client $name")
      rawStream(config, name, 0)
      val newName = name + 1
      context.system.scheduler.scheduleOnce(1 minutes, self, StartIncreasing(newName))


    case Schedule(name) =>
      context.system.scheduler.scheduleOnce(0 milliseconds, self, StartIncreasing(name))

    case m@_ =>
      println(s"unknown message in ClientOrchestrator $m")

  }

}

object ClientOrchestratorActor{

  def props(config:Config)(implicit mat:ActorMaterializer, ec:ExecutionContext, actorSystem:ActorSystem):Props = Props(new ClientOrchestratorActor((config)))
}
