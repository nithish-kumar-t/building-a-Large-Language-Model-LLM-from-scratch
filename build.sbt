val scala3Version = "3.5.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "building-a-Large-Language-Model-LLM-from-scratch",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,

    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test
  )

libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "3.4.0"
libraryDependencies += "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "3.4.0"
libraryDependencies += "org.apache.hadoop" % "hadoop-mapreduce-client-jobclient" % "3.4.0"
