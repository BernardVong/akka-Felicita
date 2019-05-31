name := "4aibd-akka-project"
version := "0.1"

lazy val akkaHttpVersion = "10.1.8"
lazy val akkaVersion    = "2.6.0-M2"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)
lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.felicita",
      scalaVersion := "2.12.8"
    )),
    name := "4aibd-akka-project",
    libraryDependencies ++= Seq(
      "com.chuusai" %% "shapeless" % "2.3.3", 
      "org.xerial" % "sqlite-jdbc" % "3.25.2" ,

      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion
    )
  )

