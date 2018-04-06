logLevel := Level.Warn
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.14")
addSbtPlugin("com.typesafe.sbt" %% "sbt-native-packager" % "1.3.3")