sbtPlugin := true

name := "sbt-quickfix"

organization := "com.dscleaver.sbt"

versionWithGit

//version := "0.4.1-LOCAL"

git.baseVersion := "0.4.1"

resolvers += "sonatype-releases" at "https://oss.sonatype.org/service/local/repositories/releases/content/"

scalacOptions += "-unchecked"

publishMavenStyle := false

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.0" % "provided"
)

publishTo <<= (version) { v =>
  def scalasbt(repo: String) = ("scalasbt " + repo, "http://repo.scala-sbt.org/scalasbt/sbt-plugin-" + repo)
  val (name, repo) = if (v.endsWith("-SNAPSHOT")) scalasbt("snapshots") else scalasbt("releases")
  Some(Resolver.url(name, url(repo))(Resolver.ivyStylePatterns))
}
