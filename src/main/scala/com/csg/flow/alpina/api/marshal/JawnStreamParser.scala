package com.csg.flow.alpina.api.marshal

import akka.stream.scaladsl.Flow
import akka.util.ByteString
import de.knutwalker.akka.stream.JsonStreamParser
import jawn.{AsyncParser, Facade}


class JawnStreamParser[J](mode: AsyncParser.Mode)(implicit facade: Facade[J]) {

  val byteStringFlow: Flow[ByteString, J, _] = {
    JsonStreamParser.flow[J](mode)
  }
}

object JawnStreamParser {

  def apply[J](mode: AsyncParser.Mode)(implicit facade: Facade[J]): JawnStreamParser[J] = {
    new JawnStreamParser[J](mode)
  }
}
