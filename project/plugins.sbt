resolvers += Resolver.sonatypeRepo("public")

val scalaJSVersion =
  Option(System.getenv("SCALAJS_VERSION")).getOrElse("1.3.1")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % scalaJSVersion)
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.0.0")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.2")
addSbtPlugin("io.github.cquiroz" % "sbt-locales" % "1.0.0")
addSbtPlugin("com.geirsson" % "sbt-ci-release" % "1.5.5")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.16")
addSbtPlugin("ch.epfl.lamp" % "sbt-dotty" % "0.4.6")
