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

libraryDependencies ++= Seq(
  "commons-codec" % "commons-codec" % "1.10" withSources() withJavadoc(),
  "com.github.nscala-time" %% "nscala-time" % "2.12.0" withSources() withJavadoc(),
  // test
  "com.github.tomakehurst" % "wiremock" % "1.58" % "test" withSources() withJavadoc() exclude("log4j", "log4j"),
  "org.mockito" % "mockito-all" % "1.10.19" % "test" withSources() withJavadoc(),
  "org.scalatest" %% "scalatest" % "2.2.6" % "test" withSources() withJavadoc(),
  "org.slf4j" % "log4j-over-slf4j" % "1.7.21" % "test" withSources() withJavadoc()
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
