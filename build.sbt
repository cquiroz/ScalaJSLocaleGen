import locales._
import sbt.Keys._
import sbtcrossproject.CrossPlugin.autoImport.{ CrossType, crossProject }

lazy val cldrApiVersion = "4.5.0"

ThisBuild / versionScheme := Some("always")

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val scalaVersion213 = "2.13.16"
lazy val scalaVersion3   = "3.3.4"
ThisBuild / scalaVersion       := scalaVersion213
ThisBuild / crossScalaVersions := Seq("2.12.20", scalaVersion213, scalaVersion3)

ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches +=
  RefPredicate.StartsWith(Ref.Tag("v"))

lazy val java17 = JavaSpec.temurin("17")
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("8"), java17)

ThisBuild / githubWorkflowBuildPreamble +=
  WorkflowStep.Run(
    List("sudo apt-get install libutf8proc-dev"),
    name = Some("Install libutf8proc")
  )
ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    List("ci-release"),
    env = Map(
      "PGP_PASSPHRASE"    -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET"        -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
    )
  )
)

ThisBuild / githubWorkflowBuildMatrixFailFast := Some(false)

val commonSettings: Seq[Setting[_]] = Seq(
  scalacOptions ~= (_.filterNot(
    Set(
      "-Wdead-code",
      "-Ywarn-dead-code",
      "-Wunused:params",
      "-Ywarn-unused:params",
      "-Wvalue-discard",
      "-Ywarn-value-discard"
    )
  )),
  Compile / doc / scalacOptions := Seq()
)

inThisBuild(
  List(
    organization := "io.github.cquiroz",
    homepage     := Some(url("https://github.com/cquiroz/scala-java-locales")),
    licenses     := Seq("BSD 3-Clause License" -> url("https://opensource.org/licenses/BSD-3-Clause")),
    developers   := List(
      Developer("cquiroz",
                "Carlos Quiroz",
                "carlos.m.quiroz@gmail.com",
                url("https://github.com/cquiroz")
      ),
      Developer("er1c", "Eric Peters", "", url("https://github.com/er1c")),
      Developer("alonsodomin", "A. Alonso Dominguez", "", url("https://github.com/alonsodomin")),
      Developer("mkotsbak", "Marius B. Kotsbak", "", url("https://github.com/mkotsbak")),
      Developer("TimothyKlim", "Timothy Klim", "", url("https://github.com/TimothyKlim"))
    )
  )
)

lazy val root = project
  .in(file("."))
  .settings(commonSettings)
  .settings(
    publish / skip := true
  )
  .aggregate(
    core.js,
    core.jvm,
    core.native,
    tests.js,
    tests.jvm,
    tests.native,
    localesFullDb.js,
    localesFullDb.native,
    localesFullCurrenciesDb.js,
    localesFullCurrenciesDb.native,
    localesMinimalEnDb.js,
    localesMinimalEnDb.native,
    localesMinimalEnUSDb.js,
    localesMinimalEnUSDb.native,
    demo.js,
    demo.native
  )

lazy val core = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .crossType(CrossType.Full)
  .in(file("core"))
  .settings(commonSettings)
  .settings(
    name := "scala-java-locales",
    libraryDependencies ++= Seq(
      "io.github.cquiroz" %%% "cldr-api" % cldrApiVersion
    ),
    scalacOptions ~= (_.filterNot(
      Set(
        "-deprecation",
        "-Xfatal-warnings"
      )
    ))
  )
  .jsSettings(
    scalacOptions ++= {
      if (scalaVersion.value == scalaVersion3)
        Seq("-scalajs-genStaticForwardersForNonTopLevelObjects")
      else Seq("-P:scalajs:genStaticForwardersForNonTopLevelObjects")
    },
    scalacOptions ++= {
      if (scalaVersion.value == scalaVersion3) Seq.empty
      else {
        val tagOrHash =
          if (isSnapshot.value) sys.process.Process("git rev-parse HEAD").lineStream_!.head
          else s"v${version.value}"
        (Compile / sourceDirectories).value.map { dir =>
          val a = dir.toURI.toString
          val g =
            "https://raw.githubusercontent.com/cquiroz/scala-java-locales/" + tagOrHash + "/core/src/main/scala"
          s"-P:scalajs:mapSourceURI:$a->$g/"
        }
      }
    }
  )
  .nativeSettings {
    scalacOptions += "-P:scalanative:genStaticForwardersForNonTopLevelObjects"
  }

lazy val cldrDbVersion = "36.0"

lazy val localesFullCurrenciesDb = crossProject(JSPlatform, NativePlatform)
  .in(file("localesFullCurrenciesDb"))
  .settings(commonSettings)
  .configure(_.enablePlugins(LocalesPlugin))
  .settings(
    name                                          := "locales-full-currencies-db",
    cldrVersion                                   := CLDRVersion.Version(cldrDbVersion),
    localesFilter                                 := LocalesFilter.All,
    nsFilter                                      := NumberingSystemFilter.All,
    calendarFilter                                := CalendarFilter.All,
    currencyFilter                                := CurrencyFilter.All,
    supportDateTimeFormats                        := true,
    supportNumberFormats                          := true,
    supportISOCodes                               := true,
    libraryDependencies += ("org.portable-scala" %%% "portable-scala-reflect" % "1.1.3")
      .cross(CrossVersion.for3Use2_13)
  )

lazy val localesFullDb = crossProject(JSPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("localesFullDb"))
  .settings(commonSettings)
  .configure(_.enablePlugins(LocalesPlugin))
  .settings(
    name                                          := "locales-full-db",
    cldrVersion                                   := CLDRVersion.Version(cldrDbVersion),
    localesFilter                                 := LocalesFilter.All,
    nsFilter                                      := NumberingSystemFilter.All,
    calendarFilter                                := CalendarFilter.All,
    currencyFilter                                := CurrencyFilter.None,
    supportDateTimeFormats                        := true,
    supportNumberFormats                          := true,
    supportISOCodes                               := true,
    libraryDependencies += ("org.portable-scala" %%% "portable-scala-reflect" % "1.1.3")
      .cross(CrossVersion.for3Use2_13)
  )

lazy val localesMinimalEnDb = crossProject(JSPlatform, NativePlatform)
  .in(file("localesMinimalEnDb"))
  .settings(commonSettings)
  .configure(_.enablePlugins(LocalesPlugin))
  .settings(
    name                                          := "locales-minimal-en-db",
    cldrVersion                                   := CLDRVersion.Version(cldrDbVersion),
    localesFilter                                 := LocalesFilter.Minimal,
    nsFilter                                      := NumberingSystemFilter.Minimal,
    calendarFilter                                := CalendarFilter.Minimal,
    currencyFilter                                := CurrencyFilter.None,
    supportDateTimeFormats                        := true,
    supportNumberFormats                          := true,
    supportISOCodes                               := false,
    libraryDependencies += ("org.portable-scala" %%% "portable-scala-reflect" % "1.1.3")
      .cross(CrossVersion.for3Use2_13)
  )

lazy val localesMinimalEnUSDb = crossProject(JSPlatform, NativePlatform)
  .in(file("localesMinimalEnUSDb"))
  .settings(commonSettings)
  .configure(_.enablePlugins(LocalesPlugin))
  .settings(
    name                                          := "locales-minimal-en_US-db",
    cldrVersion                                   := CLDRVersion.Version(cldrDbVersion),
    localesFilter                                 := LocalesFilter.Selection(List("en_US")),
    nsFilter                                      := NumberingSystemFilter.Minimal,
    calendarFilter                                := CalendarFilter.Minimal,
    currencyFilter                                := CurrencyFilter.None,
    supportDateTimeFormats                        := true,
    supportNumberFormats                          := true,
    supportISOCodes                               := false,
    libraryDependencies += ("org.portable-scala" %%% "portable-scala-reflect" % "1.1.3")
      .cross(CrossVersion.for3Use2_13)
  )

lazy val tests = crossProject(JVMPlatform, JSPlatform, NativePlatform)
  .in(file("tests"))
  .settings(commonSettings)
  .settings(
    publish / skip                          := true,
    name                                    := "tests",
    libraryDependencies += "org.scalameta" %%% "munit" % "1.1.0" % Test,
    testFrameworks += new TestFramework("munit.Framework"),
    scalacOptions ~= (_.filterNot(
      Set(
        "-deprecation",
        "-Xfatal-warnings"
      )
    ))
  )
  .jsSettings(Test / parallelExecution := false,
              scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule))
  )
  .jsConfigure(_.dependsOn(core.js, macroutils, localesFullCurrenciesDb.js))
  .jvmSettings(
    // Fork the JVM test to ensure that the custom flags are set
    Test / fork                                 := true,
    // Use CLDR provider for locales
    // https://docs.oracle.com/javase/8/docs/technotes/guides/intl/enhancements.8.html#cldr
    Test / javaOptions ++= Seq(
      "-Duser.language=en",
      "-Duser.country=",
      "-Djava.locale.providers=CLDR",
      "-Dfile.encoding=UTF8",
      "-Xmx6G"
    ),
    libraryDependencies += "io.github.cquiroz" %%% "cldr-api" % cldrApiVersion,
    Test / classLoaderLayeringStrategy          := ClassLoaderLayeringStrategy.Flat
  )
  .jvmConfigure(_.dependsOn(macroutils))
  .nativeConfigure(_.dependsOn(core.native, macroutils, localesFullCurrenciesDb.native))
  .platformsSettings(JSPlatform, NativePlatform)(
    Test / unmanagedSourceDirectories += baseDirectory.value.getParentFile / "js-native" / "src" / "test" / "scala"
  )
  .platformsSettings(JSPlatform, JVMPlatform)(
    Test / unmanagedSourceDirectories += baseDirectory.value.getParentFile / "js-jvm" / "src" / "test" / "scala"
  )

lazy val macroutils = project
  .in(file("macroutils"))
  .settings(commonSettings)
  .settings(
    name                    := "macroutils",
    libraryDependencies ++= {
      if (scalaVersion.value == scalaVersion3) Seq.empty
      else Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value)
    },
    scalacOptions ~= (_.filterNot(
      Set(
        "-deprecation",
        "-Xfatal-warnings"
      )
    )),
    Compile / doc / sources := {
      if (scalaVersion.value == scalaVersion3) Seq() else (Compile / doc / sources).value
    }
  )

lazy val demo = crossProject(JSPlatform, NativePlatform)
  .in(file("demo"))
  .settings(commonSettings)
  .settings(
    publish / skip                  := true,
    scalaJSUseMainModuleInitializer := true,
    name                            := "demo"
  )
  .dependsOn(core)
