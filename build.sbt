import CommonResolvers._

publishTo <<= version { v: String =>
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at s"$nexus/snapshots")
  else
    Some("releases" at s"$nexus/releases")
}

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

name := "vehicles-presentation-common"

organization := "dvla"

version := "1.0-SNAPSHOT"

scalacOptions := Seq(
  "-deprecation",
  "-unchecked",
  "-feature",
  "-Xlint",
  "-language:reflectiveCalls",
  "-Xmax-classfile-name", "128"
)

// Include assets when publishing jars
artifact in (Compile, packageBin) ~= { (art: Artifact) =>
  art.copy(`classifier` = Some("asset"))
}

// Disable documentation generation to save time for the CI build process
sources in doc in Compile := List()

libraryDependencies ++= Seq(
  cache,
  ws,
  "commons-codec" % "commons-codec" % "1.9" withSources() withJavadoc(),
  "com.google.inject" % "guice" % "4.0-beta4" withSources() withJavadoc(),
  "com.tzavellas" % "sse-guice" % "0.7.1" withSources() withJavadoc(), // Scala DSL for Guice
  "org.specs2" %% "specs2" % "2.4" % "test" withSources() withJavadoc(),
  "org.scalatest" %% "scalatest" % "2.2.1" % "test" withSources() withJavadoc(),
  "org.mockito" % "mockito-all" % "1.9.5" % "test" withSources() withJavadoc(),
  "org.slf4j" % "log4j-over-slf4j" % "1.7.7" % "test" withSources() withJavadoc(),
  "com.github.tomakehurst" % "wiremock" % "1.46" % "test" withSources() withJavadoc() exclude("log4j", "log4j")
)     

credentials += Credentials(Path.userHome / ".sbt/.credentials")
