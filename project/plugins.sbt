resolvers += Resolver.sonatypeRepo("public")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.6.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.0.0")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.2")
addSbtPlugin("io.github.cquiroz" % "sbt-locales" % "2.7.0")
addSbtPlugin("com.geirsson" % "sbt-ci-release" % "1.5.7")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.20")
addSbtPlugin("ch.epfl.lamp" % "sbt-dotty" % "0.5.5")
addSbtPlugin("com.codecommit" % "sbt-github-actions" % "0.9.5")
