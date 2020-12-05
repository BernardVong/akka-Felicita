
name := "4aibd-akka-project"

version := "0.1"

scalaVersion := "2.13.4"

// https://mvnrepository.com/artifact/com.chuusai/shapeless
libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.3"

// https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc
libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.32.3.2"
// https://mvnrepository.com/artifact/com.typesafe.akka/akka-http
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.2.1"

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-stream
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.10"

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-http-spray-json
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.1"

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-http-testkit
libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit" % "10.2.1" % Test

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-testkit
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.6.10" % Test

// https://mvnrepository.com/artifact/org.scalactic/scalactic
libraryDependencies += "org.scalactic" %% "scalactic" % "3.1.0"
// https://mvnrepository.com/artifact/org.scalatest/scalatest
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0" % Test


/*libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest-funsuite" % "3.2.0" % "test",
  "org.scalatest" %% "scalatest-mustmatchers" % "3.2.0" % "test",
  "org.scalatest" %% "scalatest-wordspec" % "3.2.0" % "test"
)*/


