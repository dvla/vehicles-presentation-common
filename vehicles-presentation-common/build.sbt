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
