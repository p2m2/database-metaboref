ThisBuild / scalaVersion := "2.12.7"
ThisBuild / organization := "com.example"

lazy val root = (project in file("."))
  .settings(
    libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
    libraryDependencies += "org.apache.commons" % "commons-text" % "1.9"
  )

lazy val util = project.in(file("./src/inrae/p2m2/util"))
                .dependsOn(root)

lazy val foodb = project.in(file("./src/inrae/p2m2/app/foodb"))
              .settings(
                name := "foodb"
              )
              .dependsOn(util)