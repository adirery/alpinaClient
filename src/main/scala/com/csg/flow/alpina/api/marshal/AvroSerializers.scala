package com.csg.flow.alpina.api.marshal

import com.csg.flow.alpina.api.sink.ApiMetrics
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.avro.specific.SpecificDatumWriter

object AvroSerializers {

  implicit object ApiMetricSerializer extends ToAvro[ApiMetrics]{

    lazy val AVRO_SCHEMA =
      """
        |{"name":"apiMetric",
        |"type":"record",
        |"fields" :[{
        |   "type": "string",
        |   "optional": false,
        |   "field": "apiName",
        |   "name": "apiName"
        |   },{
        |   "type": "long",
        |   "optional": false,
        |   "logicalType":"timestamp-millis",
        |   "field": "timestamp",
        |   "version":1,
        |   "name": "timestamp"
        |   },{
        |   "type": "double",
        |   "optional": false,
        |   "field": "averageLatency",
        |   "name": "averageLatency"
        |   },{
        |   "type": "double",
        |   "optional": false,
        |   "field": "stdDeviationLatency",
        |   "name": "stdDeviationLatency"
        |   },{
        |   "type": "long",
        |   "optional": false,
        |   "field": "minLatency",
        |   "name": "minLatency"
        |   },{
        |   "type": "long",
        |   "optional": false,
        |   "field": "maxLatency",
        |   "name": "maxLatency"
        |   },{
        |   "type": "long",
        |   "optional": false,
        |   "field": "numMessages",
        |   "name": "numMessages"
        |   },{
        |   "type": "long",
        |   "optional": false,
        |   "field": "numBytes",
        |   "name": "numBytes"
        |   }],
        |   "optional":false
      }""".stripMargin

    def asAvroRecord(element:ApiMetrics)={

      val schema = new Schema.Parser().parse(AVRO_SCHEMA)
      val genericRecord = new GenericData.Record(schema)
      genericRecord.put("apiName", element.name)
      genericRecord.put("timestamp", element.timestamp)
      genericRecord.put("averageLatency", element.averageLatency)
      genericRecord.put("stdDeviationLatency", element.stdDeviationLatency)
      genericRecord.put("minLatency", element.minLatency)
      genericRecord.put("maxLatency", element.maxLatency)
      genericRecord.put("numMessages", element.numMessages)
      genericRecord.put("numBytes", element.numBytes)
      (genericRecord, new SpecificDatumWriter[GenericRecord](schema))

    }

  }

}
