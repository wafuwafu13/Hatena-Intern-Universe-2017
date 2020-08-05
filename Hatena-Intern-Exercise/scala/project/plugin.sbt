// Comment to get more information during initialization
logLevel := Level.Info

resolvers ++= Seq(
    DefaultMavenRepository,
    Classpaths.typesafeReleases,
    Classpaths.typesafeSnapshots,
    Classpaths.sbtPluginReleases
)

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.6.0")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")
