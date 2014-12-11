import Common._
import net.litola.SassPlugin

name := "vehicles-presentation-common"

version := versionString

organization := organisationString

organizationName := organisationNameString

scalaVersion := scalaVersionString

scalacOptions := scalaOptionsSeq

publishTo <<= publishResolver

credentials += sbtCredentials

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

crossScalaVersions := Seq("2.10.3", "2.11.4")

val root = project.in(file(".")).enablePlugins(PlayScala, SbtWeb)

val commonTests = project.in(file(testProjectName))
  .dependsOn(root % "compile->test" )
  .enablePlugins(PlayScala, SassPlugin, SbtWeb)

addCommandAlias("all-tests", ";test;commonTests/test")

addCommandAlias("all-clean", ";clean;commonTests/clean")

addCommandAlias("all-tests-clean", ";clean;commonTests/clean;test;commonTests/test")

addCommandAlias("common-tests-run", ";project commonTests;run;project root")

// Uncomment next line when released and before publishing to github. NOTE: bintray plugin doesn't work with SNAPSHOTS
//bintrayPublishSettings

BintrayCredentials.bintrayCredentialsTask

// Disable documentation generation to save time for the CI build process
sources in doc in Compile := List()

libraryDependencies ++= Seq(
  cache,
  ws,
  "commons-codec" % "commons-codec" % "1.9" withSources() withJavadoc(),
  "com.google.inject" % "guice" % "4.0-beta4" withSources() withJavadoc(),
  "com.tzavellas" % "sse-guice" % "0.7.1" withSources() withJavadoc(), // Scala DSL for Guice
  "org.scalatest" %% "scalatest" % "2.2.1" % "test" withSources() withJavadoc(),
  "org.mockito" % "mockito-all" % "1.9.5" % "test" withSources() withJavadoc(),
  "org.slf4j" % "log4j-over-slf4j" % "1.7.7" % "test" withSources() withJavadoc(),
  "com.github.nscala-time" %% "nscala-time" % "1.4.0" withSources() withJavadoc(),
  "com.github.detro" % "phantomjsdriver" % "1.2.0" % "test" withSources() withJavadoc(),
  "com.github.tomakehurst" % "wiremock" % "1.46" % "test" withSources() withJavadoc() exclude("log4j", "log4j")
)

instrumentSettings

ScoverageKeys.excludedPackages := "<empty>;Reverse.*"

CoverallsPlugin.coverallsSettings

net.virtualvoid.sbt.graph.Plugin.graphSettings

sbt.Keys.fork in Test := false

publishArtifact in (Test, packageBin) := true
