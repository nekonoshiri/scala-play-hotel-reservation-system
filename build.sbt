name := """scala-play-hotel-reservation-system"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "3.6.4"

libraryDependencies ++= Seq(
  evolutions,
  guice,
  jdbc,
  jdbc % Test,
  "com.h2database" % "h2" % "2.3.232",
  "org.playframework.anorm" %% "anorm" % "2.8.1",
  "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
