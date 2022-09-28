name := "play-java-websocket-example"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.12.6"

crossScalaVersions := Seq("2.11.12", "2.12.6")

// https://github.com/sbt/junit-interface
testOptions += Tests.Argument(TestFrameworks.JUnit, "-a", "-v")

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-deprecation", "-Xlint")

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.27"
libraryDependencies += "org.webjars" %% "webjars-play" % "2.6.2"
libraryDependencies += "org.webjars" % "bootstrap" % "2.3.2"
libraryDependencies += "org.webjars" % "flot" % "0.8.3"


// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.assertj" % "assertj-core" % "3.8.0" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "3.0.0" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "2.1.0" % Test

LessKeys.compress := true
