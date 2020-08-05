import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

import scalariform.formatter.preferences._

val appName = "hatena-intern-exercise"
val appVersion  = "0.0.1"
val appScalaVersion = "2.11.11"

val main = Project(
  appName,
  base = file("."),
  settings = Seq(
    version := appVersion,
    scalaVersion := appScalaVersion,
    libraryDependencies ++= Seq(
      "joda-time" % "joda-time" % "2.9.9",
      "org.scalatest" %% "scalatest" % "3.0.1" % "test",
      "com.github.scala-incubator.io" %% "scala-io-core" % "0.4.3",
      "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.3"
    ),
    resolvers ++= Seq(
      "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
    ),
    fork in Test := true,
    scalacOptions in Test ++= Seq("-Yrangepos")
  ) ++  formatSettings
).settings(SbtScalariform.scalariformSettings: _*)

lazy val formatSettings = Seq(
  ScalariformKeys.preferences := FormattingPreferences()
    .setPreference(DoubleIndentClassDeclaration, true)
    .setPreference(DanglingCloseParenthesis, Preserve)
)

