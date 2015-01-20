organization := "me.lessis"

name := "base64"

version := "0.2.0-SNAPSHOT"

licenses := Seq(
  ("MIT", url(s"https://github.com/softprops/${name.value}/blob/${version.value}/LICENSE")))

homepage := Some(url(s"https://github.com/softprops/${name.value}/#readme"))

scalacOptions += Opts.compile.deprecation

crossScalaVersions := Seq("2.10.4", "2.11.5")

scalaVersion := crossScalaVersions.value.last

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "commons-codec" % "commons-codec" % "1.9" % "test",
  "io.netty" % "netty-codec" % "4.0.23.Final" % "test")

seq(bintraySettings:_*)
 
bintray.Keys.packageLabels in bintray.Keys.bintray := Seq("base64", "encoding", "rfc4648")

seq(lsSettings:_*)

LsKeys.tags in LsKeys.lsync := (bintray.Keys.packageLabels in bintray.Keys.bintray).value

externalResolvers in LsKeys.lsync := (resolvers in bintray.Keys.bintray).value

seq(cappiSettings:_*)

pomExtra := (
  <scm>
    <url>git@github.com:softprops/{name.value}.git</url>
    <connection>scm:git:git@github.com:softprops/{name.value}.git</connection>
  </scm>
  <developers>
    <developer>
      <id>softprops</id>
      <name>Doug Tangren</name>
      <url>https://github.com/softprops</url>
    </developer>
  </developers>
  <url>https://github.com/softprops/{name.value}</url>)
