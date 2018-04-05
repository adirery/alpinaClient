package com.csg.flow.alpina.api.model

case class AssetServicingProperties(
                                  messageType:String,
                                  sessionId:String,
                                  sequenceId:Long,
                                  format:String,
                                  timestamp:String,
                                  source:String,
                                  destination:String,
                                  version:String,
                                  signature:String
                                )

case class AssetServicingMessage(
                                 properties: AssetServicingProperties,
                                 payload:String
                                )
