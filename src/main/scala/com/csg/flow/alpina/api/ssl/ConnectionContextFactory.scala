package com.csg.flow.alpina.api.ssl

import java.io.FileInputStream
import java.security.{KeyStore, SecureRandom}

import akka.http.scaladsl.ConnectionContext
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}

object ConnectionContextFactory {

  def connectionContext(isSecure:Boolean) = {
    if(isSecure) {
      val ks = KeyStore.getInstance("PKCS12")
      val keyStore = new FileInputStream("/home/ec2-user/certs/posttradeutility.p12")
      val ksPassword = "Password1".toCharArray
      require(keyStore != null, "Keystore Required")
      ks.load(keyStore, ksPassword)
      val keyManagerFactory = KeyManagerFactory.getInstance("SunX509")
      keyManagerFactory.init(ks, ksPassword)

      val ts = KeyStore.getInstance("JKS")
      val trustStore = new FileInputStream("/home/ec2-user/certs/cacert-added-then-cert-nokey.jks")
      val tsPassword = "Password1".toCharArray
      require(trustStore != null, "Truststore Required")
      ts.load(trustStore, tsPassword)
      val trustyManagerFactory = TrustManagerFactory.getInstance("SunX509")
      trustyManagerFactory.init(ts)

      val sslContext = SSLContext.getInstance("TLS")
      sslContext.init(keyManagerFactory.getKeyManagers, trustyManagerFactory.getTrustManagers, new SecureRandom)
      Some(ConnectionContext.https(sslContext))
    }else{
      None
    }
  }

}
