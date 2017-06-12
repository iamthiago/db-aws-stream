name := "db-aws-streams"

version := "1.0"

scalaVersion := "2.12.1"

val akkaV = "2.5.2"
val alpakkaV = "0.9"
val akkaHttpV = "10.0.7"
val slickV = "3.2.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaV,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaV % Test,
  "com.typesafe.akka" %% "akka-actor" % akkaV,
  "com.typesafe.akka" %% "akka-testkit" % akkaV % Test,
  "com.typesafe.akka" %% "akka-http" % akkaHttpV,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV % Test,
  "com.lightbend.akka" %% "akka-stream-alpakka-sns" % alpakkaV,
  "com.typesafe.slick" %% "slick" % slickV,
  "com.typesafe.slick" %% "slick-hikaricp" % slickV,
  "com.github.tminglei" %% "slick-pg" % "0.15.0",
  "org.postgresql" % "postgresql" % "9.4.1212",
  "net.sourceforge.jtds" % "jtds" % "1.3.1"
)
