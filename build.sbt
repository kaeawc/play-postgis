name := "play-postgis"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "org.postgis" % "postgis-jdbc" % "1.3.3",
  "joda-time" % "joda-time" % "2.3"
)

scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",
  "-deprecation",
  "-unchecked",
  "-feature",
  "-language:postfixOps",
  "-language:implicitConversions"
)

play.Project.playScalaSettings
