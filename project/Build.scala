import sbt._
//import com.github.siasia._
import Keys._
import sbt.Package._
import sbtrelease._
import sbtrelease.ReleasePlugin._
import net.virtualvoid.sbt.graph.Plugin.graphSettings
import sbtrelease.ReleasePlugin.ReleaseKeys._
import sbtrelease.ReleaseStateTransformations._
import aether.Aether._


object TopLevelBuild extends Build {
  import BuildSettings._

  lazy val buildSettings = Seq(
    organization := "net.thecoda.gocd-scala-plugin-api",
    scalaVersion := "2.11.0",
    scalacOptions := Seq("-feature", "-deprecation", "-unchecked", "-Xlint", "-encoding", "utf8", "-Yrangepos"),
    scalacOptions in (console) += "-Yrangepos"
  )

  // If macros turn out to be useful, break this out into core and a separate macros subproject
  lazy val root = Project(id = "root", base = file("."))
    .aggregate(core, examples)
    .settings(commonSettings : _*)

  lazy val core = Project(id = "core", base = file("core"))
    .configs(IntegrationTest)
    .settings(commonSettings : _*)

  lazy val examples = Project(id = "examples", base = file("examples"))
    .dependsOn(core)
    .configs(IntegrationTest)
    .settings(commonSettings : _*)

  lazy val commonSettings = Defaults.defaultSettings ++
    sbtPromptSettings ++
    buildSettings ++
    graphSettings ++
    releaseSettings ++
    Defaults.itSettings ++
    commonDeps //:+
    //addCompilerPlugin("org.scalamacros" %% "paradise" % "2.0.0-M3")
    //publishSettings ++
    //releaseSettings ++
    //packagingSettings ++
    //aetherSettings ++
    //aetherPublishSettings ++

  lazy val commonDeps = Seq(
    resolvers ++= Resolvers.all,
    ivyXML := ivyDeps,
    libraryDependencies ++= Dependencies.core,
    libraryDependencies ++= Dependencies.test,
    libraryDependencies <++= (scalaVersion)(sv =>
      Seq(
        "org.scala-lang" % "scala-reflect" % sv,
        "org.scala-lang" % "scala-compiler" % sv
      )
    )
    //testOptions in Test += Tests.Argument("console") //, "junitxml")
  )


  lazy val publishSettings: Seq[Setting[_]] = Seq(
    credentials += Credentials(Path.userHome / ".ivy2" / "codanet.travis.credentials"),
    publishMavenStyle := true,
    publishTo := Some("Codanet Travis" at "https://api.bintray.com/maven/thecoda/maven/gocd-scala-plugin-api")
  )

  lazy val releaseSettings = Seq(
    releaseVersion := { ver => ciBuildNum.getOrElse(versionFormatError) },
    nextVersion    := { ver => ciBuildNum.getOrElse(versionFormatError) },
    releaseProcess := Seq[ReleaseStep](
      //checkSnapshotDependencies,
      inquireVersions,
      //runTest,
      setReleaseVersion,
      commitReleaseVersion, // performs the initial git checks
      tagRelease,
      publishArtifacts     // checks whether `publishTo` is properly set up
      //setNextVersion,
      //commitNextVersion
      //pushChanges           // also checks that an upstream branch is properly configured
    )
  )

  lazy val packagingSettings = Seq(
    packageOptions <<= (Keys.version, Keys.name, Keys.artifact) map {
      (version: String, name: String, artifact: Artifact) =>
        Seq(ManifestAttributes(
          "Implementation-Vendor" -> "thecoda.net",
          "Implementation-Title" -> "gocd-scala-plugin-api",
          "Version" -> version,
          "Build-Number" -> ciBuildNum.getOrElse("n/a"),
          "Group-Id" -> name,
          "Artifact-Id" -> artifact.name,
          "Git-SHA1" -> Git.hash,
          "Git-Branch" -> Git.branch,
          "Built-By" -> "Oompa-Loompas",
          "Build-Jdk" -> prop("java.version"),
          "Built-When" -> (new java.util.Date).toString,
          "Build-Machine" -> java.net.InetAddress.getLocalHost.getHostName
        )
      )
    }
  )

  val ivyDeps = {
    <dependencies>
      <!-- commons logging is evil. It does bad, bad things to the classpath and must die. We use slf4j instead -->
      <exclude module="commons-logging"/>
    </dependencies>
  }
}
