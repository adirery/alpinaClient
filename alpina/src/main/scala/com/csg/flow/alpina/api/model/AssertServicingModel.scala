package com.csg.flow.alpina.api.model

case class AssetServicingProperties(
                                  format:String,
                                  source:String,
                                  timestamp:String,
                                  signature:String,
                                  messageType:String,
                                  version:String,
                                  sessionId:String,
                                  destination:String,
                                  sequenceId:Long
                                )

case class AssetServicingMessage(
                                 properties: AssetServicingProperties,
                                 payload:String
                                )
