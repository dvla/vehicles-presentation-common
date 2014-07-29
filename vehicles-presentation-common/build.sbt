import CommonResolvers._

publishTo <<= version { v: String =>
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at s"$nexus/snapshots")
  else
    Some("releases" at s"$nexus/releases")
}

name := "vehicles-presentation-common"

organization := "dvla"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)     

play.Project.playScalaSettings

credentials += Credentials(Path.userHome / ".sbt/.credentials")
