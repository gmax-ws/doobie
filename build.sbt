name := "doobie"

version := "0.1"

scalaVersion := "2.13.4"

val doobieVersion = "0.10.0"
val catsVersion = "2.3.1"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-effect" % catsVersion,
  "org.tpolecat" %% "doobie-core"      % doobieVersion,
  "org.tpolecat" %% "doobie-h2"        % doobieVersion,          // H2 driver 1.4.200 + type mappings.
  "org.tpolecat" %% "doobie-hikari"    % doobieVersion,          // HikariCP transactor.
  "org.tpolecat" %% "doobie-postgres"  % doobieVersion,          // Postgres driver 42.2.12 + type mappings.
  "org.tpolecat" %% "doobie-quill"     % doobieVersion,          // Support for Quill 3.5.1
  "org.tpolecat" %% "doobie-specs2"    % doobieVersion % "test", // Specs2 support for typechecking statements.
  "org.tpolecat" %% "doobie-scalatest" % doobieVersion % "test"  // ScalaTest support for typechecking statements.

)