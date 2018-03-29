package com.csg.flow.alpina.api.marshal

import org.apache.avro.generic.GenericRecord
import org.apache.avro.specific.SpecificDatumWriter

trait ToAvro[T] {

  def asAvroRecord(element:T):(GenericRecord, SpecificDatumWriter[GenericRecord])

}
