import sbt._
import Keys._

object ExampleBuild extends Build {
  import Dependencies._
  import Settings._

  lazy val root = Project(
    id = "root",
    base = file("root"),
    settings = sharedSettings ++ Seq(
      dontPackage
    )
  ) aggregate (plugin, tests)

  lazy val plugin = Project(
    id   = "example",
    base = file("plugin"),
    settings = publishableSettings ++ mergeDependencies ++ Seq(
      libraryDependencies += compiler(languageVersion),
      libraryDependencies += scalameta,
      libraryDependencies += scalahost
    )
  )

  lazy val sandbox = Project(
    id   = "sandbox",
    base = file("sandbox"),
    settings = sharedSettings ++ Seq(
      usePlugin(plugin)
    )
  ) dependsOn (plugin)

  lazy val tests = Project(
    id   = "tests",
    base = file("tests"),
    settings = sharedSettings ++ Seq(
      usePlugin(plugin),
      libraryDependencies ++= Seq(scalatest, scalacheck),
      dontPackage
    ) ++ exposeClasspaths("tests")
  ) dependsOn (plugin)
}