import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import ScalateKeys._
import NativePackagerHelper._

val scalatraVersion = "2.5.1"

lazy val internbookmark = (project in file("."))
  .settings(ScalatraPlugin.scalatraWithJRebel: _*)
  .settings(scalateSettings: _*)
  .settings(
    name := "scala-Intern-Bookmark",
    version := "0.0.3",
    scalaVersion := "2.11.11",
    libraryDependencies ++= Seq(
      "joda-time" % "joda-time" % "2.9.9",
      "org.joda" % "joda-convert" % "1.8.1",
      "com.typesafe.slick" %% "slick" % "3.1.1",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1",
      "com.github.tarao" %% "slick-jdbc-extension" % "0.0.7",
      "com.github.tototoshi" %% "slick-joda-mapper" % "2.2.0",
      "mysql" % "mysql-connector-java" % "5.1.42",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.json4s" %% "json4s-jackson" % "3.5.2",
      "org.json4s" %% "json4s-ext" % "3.5.2",
      "org.scalatest" %% "scalatest" % "2.2.6" % "test",
      "org.scalatra" %% "scalatra" % scalatraVersion,
      "org.scalatra" %% "scalatra-scalatest" % scalatraVersion,
      "org.scalatra" %% "scalatra-json" % scalatraVersion,
      "org.scalatra" %% "scalatra-auth" % scalatraVersion,
      "org.eclipse.jetty" % "jetty-webapp" % "9.4.6.v20170531" % "container;compile",
      "org.eclipse.jetty" % "jetty-plus" % "9.4.6.v20170531" % "container",
      "javax.servlet" % "javax.servlet-api" % "3.1.0",
      "org.scalaj" %% "scalaj-http" % "2.3.0",
      "com.github.takezoe" %% "blocking-slick-31" % "0.0.7"
    ),
    fork in Test := true,
    fork in run := true,
    mainClass in (Compile, run) := Some("internbookmark.cli.BookmarkCli"),
    mainClass in reStart := Some("JettyLauncher"),
    javaOptions in Test += "-Dconfig.resource=test.conf",
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature",
      "-Xlint",
      "-Xlint:-missing-interpolator",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Ywarn-unused",
      "-Ywarn-value-discard"
    ),
    initialCommands := "import internbookmark._",
    TwirlKeys.templateImports ++= Seq("internbookmark.web.ViewContext", "internbookmark.model._")
  )
  .enablePlugins(SbtTwirl)
  .enablePlugins(JettyPlugin)
  .enablePlugins(JavaAppPackaging)
  .settings(
    mappings in Universal ++= {
      val staticDirectory = (sourceDirectory in Compile).value / "webapp"
      staticDirectory.*** pair relativeTo(baseDirectory.value)
    }
  )
