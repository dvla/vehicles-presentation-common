// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-web" % "1.1.0")

// Plugin for publishing scoverage results to coveralls
addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "0.99.10.2")

addSbtPlugin("org.scoverage" %% "sbt-coveralls" % "0.99.0")

resolvers += Resolver.url(
  "bintray-sbt-plugin-releases",
  url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
    Resolver.ivyStylePatterns)

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.1.2")

addSbtPlugin("net.litola" % "play-sass" % "0.4.0")