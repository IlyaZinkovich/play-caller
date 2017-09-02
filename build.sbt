name := "play-caller"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += Resolver.sonatypeRepo("snapshots")

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  guice,
  ws,
  ehcache,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test,
  "com.googlecode.libphonenumber" % "libphonenumber" % "8.8.0"
)

maintainer := "Ilya Zinkovich"
dockerUsername := Some("ilyazinkovich")
dockerExposedPorts in Docker := Seq(9000, 9443)

javaOptions in Universal ++= Seq(
  "-Dpidfile.path=/dev/null"
)