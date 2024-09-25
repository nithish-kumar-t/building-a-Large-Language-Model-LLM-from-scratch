val scala3Version = "3.5.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "LLM-hw1",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
//    assembly / mainClass := Some("main.scala.MyMapReduceJob"),

    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "org.apache.hadoop" % "hadoop-common" % "3.4.0",
      "org.apache.hadoop" % "hadoop-mapreduce-client-core" % "3.4.0",
      "org.apache.hadoop" % "hadoop-mapreduce-client-jobclient" % "3.4.0",
      "com.knuddels" % "jtokkit" % "0.6.1"

//      "com.jtokkit" % "jtokkit" % "1"
    ),
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    }

  )

//val jarName = "MapReduce.jar"
//assembly / assemblyJarName := jarName