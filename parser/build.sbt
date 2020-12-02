ThisBuild / scalaVersion := "2.13.1"
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

lazy val flavonoids = project.in(file("./src/inrae/p2m2/app/FlavonoidsCombinatoire"))
              .settings(
                name := "flavonoids",
                //"org.apache.poi" % "poi" % "4.1.2",
                libraryDependencies += "org.apache.tika" % "tika-core" % "1.25",
                libraryDependencies += "org.apache.tika" % "tika-parsers" % "1.25"
              )
              .dependsOn(util)