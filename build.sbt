name := "twitter"

version := "0.1"

scalaVersion := "2.12.7"

scalacOptions += "-Ypartial-unification"

libraryDependencies ++= {
  lazy val akkaHttpVersion = "10.1.3"
  lazy val akkaVersion = "2.5.4"
  lazy val circeVersion = "0.9.3"
  lazy val macwireVersion = "2.3.1"

  Seq(
    // Web ------------------------------------------------------------------
    //
    "com.typesafe.akka" %% "akka-actor"        % akkaVersion,
    "com.typesafe.akka" %% "akka-stream"       % akkaVersion,
    "com.typesafe.akka" %% "akka-http"         % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-caching" % akkaHttpVersion,
    // DI -------------------------------------------------------------------
    //
    "com.softwaremill.macwire" %% "macros"     % macwireVersion % "provided",
    "com.softwaremill.macwire" %% "util"       % macwireVersion,
    "com.softwaremill.macwire" %% "proxy"      % macwireVersion,
    // JSON serialization library---------------------------------------------
    //
    "io.circe"  %% "circe-core"      % circeVersion,
    "io.circe"  %% "circe-generic"   % circeVersion,
    "io.circe"  %% "circe-parser"    % circeVersion,
    // Util ------------------------------------------------------------------
    //
    "com.typesafe"                % "config"            % "1.3.3",      // Configuration
    "com.github.pureconfig"      %% "pureconfig"        % "0.9.1",      // Config file parser
    "com.wix"                    %% "accord-core"       % "0.7.2",      // Validation
    "org.typelevel"              %% "cats-core"         % "1.5.0",      // Type classes
    "ch.qos.logback"              % "logback-classic"   % "1.2.3",      // Logging
    "com.typesafe.scala-logging" %% "scala-logging"     % "3.9.0",
    "com.typesafe.akka"          %% "akka-slf4j"        % akkaVersion,
    // TESTING --------------------------------------------------------------
    "com.typesafe.akka"          %% "akka-http-testkit" % akkaHttpVersion % Test,
    "com.typesafe.akka"          %% "akka-testkit"      % akkaVersion     % Test,
    "org.mockito"                 % "mockito-all"       % "1.10.19"       % Test,
    "org.scalatest"              %% "scalatest"         % "3.0.5"         % Test
  )
}

// parallelExecution in Test := false
coverageEnabled := true

// scalastyle:off
coverageExcludedPackages := "<empty>;Reverse.*;.*(CirceSupport|FailFastUnmarshaller|ErrorAccumulatingUnmarshaller|Error|ExceptionHandler).*"
// scalastyle:on

addCommandAlias("c", "compile")
addCommandAlias("s", "scalastyle")
addCommandAlias("tc", "test:compile")
addCommandAlias("ts", "test:scalastyle")
addCommandAlias("t", "test")
addCommandAlias("to", "testOnly")
