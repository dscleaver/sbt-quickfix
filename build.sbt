sbtPlugin := true

name := "sbt-quickfix"

organization := "com.dscleaver.sbt"

scalaVersion := "2.12.8"

licenses += ("BSD 3-Clause", url("https://opensource.org/licenses/BSD-3-Clause"))

versionWithGit

//version := "0.4.1-LOCAL"

git.baseVersion := "1.0.0"

resolvers += "sonatype-releases" at "https://oss.sonatype.org/service/local/repositories/releases/content/"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xfuture",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions"
)

publishMavenStyle := false

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.5" % "provided"
)

publishTo := {
  def scalasbt(repo: String) = ("scalasbt " + repo, "http://repo.scala-sbt.org/scalasbt/sbt-plugin-" + repo)
  val (name, repo) = if (version.value.endsWith("-SNAPSHOT")) scalasbt("snapshots") else scalasbt("releases")
  Some(Resolver.url(name, url(repo))(Resolver.ivyStylePatterns))
}
