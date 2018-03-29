package com.csg.flow.alpina.api.marshal

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

import org.apache.avro.io.{BinaryEncoder, EncoderFactory}

class AvroSerializer[T] {

  val MAGIC_BYTE = 0x0
  val idSize = 4

  def serialize(element: T, schemaId:Int)(implicit converter:ToAvro[T]): Array[Byte] ={

    val out = new ByteArrayOutputStream()
    out.write(MAGIC_BYTE)
    out.write(ByteBuffer.allocate(idSize).putInt(schemaId).array())
    val encoder:BinaryEncoder = EncoderFactory.get().binaryEncoder(out, null)
    val (record, writer) = converter.asAvroRecord(element)
    writer.write(record, encoder)
    encoder.flush()
    out.close()
    out.toByteArray

}

}
