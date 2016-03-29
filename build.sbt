import com.github.retronym.SbtOneJar._

oneJarSettings

mainClass in oneJar := Some("ru.evseev.elaslack.App")

name := "ElaSlack"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "jitpack" at "https://jitpack.io"

resolvers += Resolver.sonatypeRepo("public")

libraryDependencies ++= Seq(
  "io.searchbox" % "jest" % "2.0.2",
  "org.elasticsearch" % "elasticsearch" % "2.2.1",
  "com.typesafe.akka" %% "akka-actor" % "2.4.2",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.2",
  "com.firebase" % "firebase-client" % "1.0.18",
  "com.github.flowctrl" % "slack-api" % "v1.1.0.RELEASE",
  "com.github.scopt" %% "scopt" % "3.4.0",

  // test
  "org.scalatest" % "scalatest_2.11" % "2.2.6" % "test",
  "org.mockito" % "mockito-all" % "1.10.19" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.0" % "test"
)
