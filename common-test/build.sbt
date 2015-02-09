import Common._

name := testProjectName

version := versionString

organization := organisationString

organizationName := organisationNameString

scalaVersion := scalaVersionString

scalacOptions := scalaOptionsSeq

resolvers ++= projectResolvers

publishTo <<= publishResolver

credentials += sbtCredentials

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

crossScalaVersions := Seq("2.10.3", "2.11.4")

lazy val root = project.in(file(".")).enablePlugins(PlayScala, SbtWeb)

libraryDependencies ++= Seq(
  "org.seleniumhq.selenium" % "selenium-java" % "2.42.2" % "test" withSources() withJavadoc(),
  "com.github.detro" % "phantomjsdriver" % "1.2.0" % "test" withSources() withJavadoc(),
  "org.mockito" % "mockito-all" % "1.9.5" % "test" withSources() withJavadoc(),
  "com.github.tomakehurst" % "wiremock" % "1.46" % "test" withSources() withJavadoc() exclude("log4j", "log4j"),
  "org.slf4j" % "log4j-over-slf4j" % "1.7.7" % "test" withSources() withJavadoc(),
  "org.scalatest" %% "scalatest" % "2.2.1" % "test" withSources() withJavadoc(),
  "commons-codec" % "commons-codec" % "1.9" withSources() withJavadoc(),
  "com.github.nscala-time" %% "nscala-time" % "1.4.0" withSources() withJavadoc(),
  "org.apache.httpcomponents" % "httpclient" % "4.3.4" withSources() withJavadoc()
)

val myTestOptions =
  if (System.getProperty("include") != null ) {
    Seq(testOptions in Test += Tests.Argument("include", System.getProperty("include")))
  } else if (System.getProperty("exclude") != null ) {
    Seq(testOptions in Test += Tests.Argument("exclude", System.getProperty("exclude")))
  } else Seq.empty[Def.Setting[_]]

myTestOptions

// If tests are annotated with @LiveTest then they are excluded when running sbt test
testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-l", "helpers.tags.LiveTest")

javaOptions in Test += System.getProperty("waitSeconds")

concurrentRestrictions in Global := Seq(Tags.limit(Tags.CPU, 4), Tags.limit(Tags.Network, 10), Tags.limit(Tags.Test, 4))

sbt.Keys.fork in Test := false

// Disable documentation generation to save time for the CI build process
sources in doc in Compile := List()

publishArtifact in (Test, packageBin) := true
