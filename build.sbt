sbtPlugin := true

name := "sbt-quickfix"

organization := "com.dscleaver.sbt"

scalaVersion := "2.10.6"

licenses += ("BSD 3-Clause", url("https://opensource.org/licenses/BSD-3-Clause"))

versionWithGit

//version := "0.4.1-LOCAL"

git.baseVersion := "0.4.1"

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
  "org.scalatest" %% "scalatest" % "2.2.6" % "provided"
)

publishTo <<= (version) { v =>
  def scalasbt(repo: String) = ("scalasbt " + repo, "http://repo.scala-sbt.org/scalasbt/sbt-plugin-" + repo)
  val (name, repo) = if (v.endsWith("-SNAPSHOT")) scalasbt("snapshots") else scalasbt("releases")
  Some(Resolver.url(name, url(repo))(Resolver.ivyStylePatterns))
}
