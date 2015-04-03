import SbtReleaseHelpers._
import sbtbuildinfo.Plugin._
import sbtrelease.ReleasePlugin._
import sbtrelease.ReleaseStateTransformations._
import sbtrelease.ReleaseStep
import sbtrelease.ReleasePlugin.ReleaseKeys.crossBuild

name := """akka-http-joda-time"""

scalacOptions ++= Seq("-feature")

organization := """io.github.morgaroth"""

crossScalaVersions := Seq("2.10.4", "2.11.6")

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "2.7",
  "com.typesafe.akka" %% "akka-http-experimental" % "1.0-M5",
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "1.0-M5"
)

buildInfoSettings

buildInfoKeys := Seq[BuildInfoKey](
  name, version, scalaVersion, sbtVersion, libraryDependencies, resolvers
)

buildInfoPackage := "io.github.morgaroth.utils.akka.http.jodatime.build"

sourceGenerators in Compile <+= buildInfo

sonatypeSettings

releaseSettings

crossBuild := true

ReleaseKeys.releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies, // : ReleaseStep
  inquireVersions, // : ReleaseStep
  runClean,
  runTest, // : ReleaseStep
  setReleaseVersion, // : ReleaseStep
  publishArtifactsSigned,
  finishReleaseAtSonatype,
  commitReleaseVersion, // : ReleaseStep, performs the initial git checks
  tagRelease, // : ReleaseStep
  setNextVersion, // : ReleaseStep
  commitNextVersion, // : ReleaseStep
  pushChanges // : ReleaseStep, also checks that an upstream branch is properly configured
)

publishArtifact in Test := false

pomExtra := githubPom(name.value, "Mateusz Jaje", "Morgaroth")

publishTo := publishRepoForVersion(version.value)

// Do not include log4jdbc as a dependency.
pomPostProcess := PackagingHelpers.removeTestOrSourceDependencies
