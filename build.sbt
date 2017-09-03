name := "play-caller"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += Resolver.sonatypeRepo("snapshots")

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  guice,
  ws,
  ehcache,
  "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % Test,
  "com.typesafe.akka" % "akka-testkit_2.12" % "2.5.4" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test
)

maintainer := "Ilya Zinkovich"
dockerUsername := Some("ilyazinkovich")
dockerExposedPorts in Docker := Seq(9000, 9443)

javaOptions in Universal ++= Seq(
  "-Delasticsearch.baseUrl=http://elasticsearch:9200",
  "-Dpidfile.path=/dev/null"
)