import sbt._

import Defaults._

// Comment to get more information during initialization
logLevel := Level.Info

resolvers ++= Seq(
    DefaultMavenRepository,
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
    Classpaths.typesafeReleases,
    Classpaths.typesafeSnapshots,
    Classpaths.sbtPluginReleases
)

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.6.0")
