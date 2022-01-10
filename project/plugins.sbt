resolvers += Resolver.sonatypeRepo("public")

addSbtPlugin("org.scala-js"              % "sbt-scalajs"                   % "1.8.0")
addSbtPlugin("org.portable-scala"        % "sbt-scalajs-crossproject"      % "1.1.0")
addSbtPlugin("org.scala-native"          % "sbt-scala-native"              % "0.4.2")
addSbtPlugin("org.portable-scala"        % "sbt-scala-native-crossproject" % "1.1.0")
addSbtPlugin("org.scalameta"             % "sbt-scalafmt"                  % "2.4.6")
addSbtPlugin("io.github.cquiroz"         % "sbt-locales"                   % "3.0.0")
addSbtPlugin("com.geirsson"              % "sbt-ci-release"                % "1.5.7")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat"                  % "0.1.20")
addSbtPlugin("com.codecommit"            % "sbt-github-actions"            % "0.14.2")
