resolvers += Resolver.url(
  "bintray-sbt-plugin-releases",
    url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
        Resolver.ivyStylePatterns)

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.1.2")

addSbtPlugin("me.lessis" % "ls-sbt" % "0.1.3")

addSbtPlugin("me.lessis" % "cappi" % "0.1.1")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.3")
