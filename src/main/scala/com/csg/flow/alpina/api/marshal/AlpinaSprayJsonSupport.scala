package com.csg.flow.alpina.api.marshal

import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.csg.flow.alpina.api.model.{AssetServicingMessage, AssetServicingProperties}
import com.csg.flow.alpina.api.{Measurement, Tweet}
import com.softwaremill.sttp.Response
import io.circe.generic.semiauto.deriveDecoder
import io.circe.{Decoder, Json}

import scala.concurrent.Future


object AlpinaSprayJsonSupport
    extends akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
    with spray.json.DefaultJsonProtocol {

  implicit val sprayJsonTweet = jsonFormat2(Tweet.apply)
  implicit val sprayJsonMeasurement = jsonFormat2(Measurement.apply)
  implicit val sprayASMProperties = jsonFormat9(AssetServicingProperties.apply)
  implicit val sprayASM = jsonFormat2(AssetServicingMessage.apply)

}

object AlpinaCirceSupport {

  implicit val tweetDecoder: Decoder[Tweet] = deriveDecoder[Tweet]
  implicit val asmPropsDecoder: Decoder[AssetServicingProperties] = deriveDecoder[AssetServicingProperties]
  implicit val asmDecoder: Decoder[AssetServicingMessage] = deriveDecoder[AssetServicingMessage]


  def decodeJson[A](json: Json)(implicit decoder: Decoder[A]): A = {
    val cursor = json.hcursor
    decoder(cursor) match {
      case Right(e) ⇒ e
      case Left(f)  ⇒ throw new Exception()
    }
  }

  implicit val rawResponseFromEntityUnmarshaller =
    Unmarshaller.withMaterializer[Response[Source[ByteString, _]], Either[String, Source[ByteString, _]]] {
      implicit ec ⇒ implicit mat ⇒ resp: Response[Source[ByteString, _]]⇒
        Future(resp.body)
    }


}


