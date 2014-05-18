import sbt._
import scala._

object Resolvers {
  //lazy val localm2 = "Local Maven Repository" at "file://"+Path.userHome+"/.m2/repository"
  lazy val mvncentral   = "Maven Central" at "http://repo1.maven.org/maven2/"
  lazy val typesafe     = Classpaths.typesafeReleases
  lazy val ossreleases  = "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/"
  lazy val osssnapshots = "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

  lazy val all = Seq(mvncentral, typesafe, ossreleases, osssnapshots)
}

object Dependencies {
  val core = Core.all
  val test = Test.all

  object Core {
    lazy val goPluginApi = "com.thoughtworks.go"       %  "go-plugin-api"                 % "current"  % "provided" from "http://www.thoughtworks.com/products/docs/go/current/help/resources/go-plugin-api-current.jar"
    lazy val all = Seq(goPluginApi)
  }

  object Test {
    lazy val scalatest  = "org.scalatest"              %% "scalatest"                     % "2.1.7"   % "test"
    lazy val scalacheck = "org.scalacheck"             %% "scalacheck"                    % "1.11.3"  % "test"
    lazy val mockito    = "org.mockito"                %  "mockito-core"                  % "1.9.0"   % "test"
    lazy val hamcrest   = "org.hamcrest"               %  "hamcrest-core"                 % "1.3"     % "test"

    lazy val all = Seq(scalatest, scalacheck, mockito, hamcrest)

  }

}
