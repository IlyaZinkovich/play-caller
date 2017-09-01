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
  "com.h2database" % "h2" % "1.4.194",
  "com.googlecode.libphonenumber" % "libphonenumber" % "8.8.0",
  "com.github.karelcemus" %% "play-redis" % "1.5.1"
)

maintainer := "Ilya Zinkovich"
dockerExposedPorts in Docker := Seq(9000, 9443)