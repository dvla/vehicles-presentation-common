import Common._

name := "vehicles-presentation-common"

version := versionString

organization := organisationString

organizationName := organisationNameString

scalaVersion := scalaVersionString

scalacOptions := scalaOptionsSeq

publishTo <<= publishResolver

credentials += sbtCredentials

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

lazy val root = project.in(file("."))
  .enablePlugins(PlayScala, SbtWeb)
  .settings(moduleName in Assets := "vehicles-presentation-common")

lazy val commonTests = project.in(file(testProjectName))
  .dependsOn(root % "compile->test" )
  .enablePlugins(PlayScala, SbtWeb)

addCommandAlias("all-tests", ";test;commonTests/test")

addCommandAlias("all-clean", ";clean;commonTests/clean")

addCommandAlias("all-tests-clean", ";clean;commonTests/clean;test;commonTests/test")

addCommandAlias("common-tests-run", ";project commonTests;run;project root")

// Uncomment next line when released and before publishing to github. NOTE: bintray plugin doesn't work with SNAPSHOTS
bintrayPublishSettings

BintrayCredentials.bintrayCredentialsTask

// Disable documentation generation to save time for the CI build process
sources in doc in Compile := List()

libraryDependencies ++= Seq(
  ws,
  "commons-codec" % "commons-codec" % "1.10" withSources() withJavadoc(),
  "commons-io" % "commons-io" % "2.4" withSources() withJavadoc(),
  "com.google.inject" % "guice" % "4.0" withSources() withJavadoc(),
  "com.github.nscala-time" %% "nscala-time" % "2.12.0" withSources() withJavadoc(),
  "com.rabbitmq" % "amqp-client" % "3.4.1",
  "com.tzavellas" % "sse-guice" % "0.7.1" withSources() withJavadoc(), // Scala DSL for Guice
  "net.htmlparser.jericho" % "jericho-html" % "3.4" withSources() withJavadoc(),
  "org.apache.commons" % "commons-email" % "1.2" withSources() withJavadoc(),
  "org.webjars" %% "webjars-play" % "2.3.0-3",
  "org.webjars" % "jquery" % "1.12.4",
  // test
  "org.seleniumhq.selenium" % "selenium-java" % "2.52.0" % "test",
  "com.codeborne" % "phantomjsdriver" % "1.2.1" % "test" withSources() withJavadoc(),
  "net.sourceforge.htmlunit" % "htmlunit" % "2.19" % "test" exclude("commons-collections", "commons-collections"),
  "com.github.tomakehurst" % "wiremock" % "1.58" % "test" withSources() withJavadoc() exclude("log4j", "log4j"),
  "org.mockito" % "mockito-all" % "1.10.19" % "test" withSources() withJavadoc(),
  "org.scalatest" %% "scalatest" % "2.2.6" % "test" withSources() withJavadoc(),
  "org.slf4j" % "log4j-over-slf4j" % "1.7.21" % "test" withSources() withJavadoc()
)

coverageExcludedPackages := "<empty>;Reverse.*"

coverageMinimum := 70

coverageFailOnMinimum := false

sbt.Keys.fork in Test := false

publishArtifact in (Test, packageBin) := true
