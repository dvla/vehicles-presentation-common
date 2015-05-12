// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.url("GitHub repository", url("http://shaggyyeti.github.io/releases"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.9")

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.1.0")

addSbtPlugin("default" % "sbt-sass" % "0.1.9")

// Plugin for publishing scoverage results to coveralls
addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "0.99.10.2")

addSbtPlugin("org.scoverage" %% "sbt-coveralls" % "0.99.0")

resolvers += Resolver.url(
  "bintray-sbt-plugin-releases",
  url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
    Resolver.ivyStylePatterns)

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.1.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.7")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.4")
