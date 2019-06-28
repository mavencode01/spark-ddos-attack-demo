name := "phData"

version := "1.2-snapshot"

scalaVersion := "2.11.8"

val sparkVersion = "2.4.0"

val log4j2Version = "2.11.2"

resolvers += Resolver.mavenLocal
resolvers += "Spark Packages Repo" at "http://dl.bintray.com/spark-packages/maven"

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.11" % sparkVersion, // % "provided",
  "org.apache.spark" % "spark-sql_2.11" % sparkVersion, // % "provided",

  "mysql" % "mysql-connector-java" % "8.0.15",

  "org.apache.hadoop" % "hadoop-aws" % "2.7.3" % "provided",

  "com.amazonaws" % "aws-java-sdk" % "1.7.4",

  "org.rogach" %% "scallop" % "2.0.6",
  "com.typesafe" % "config" % "1.2.1",

  "org.apache.logging.log4j" % "log4j-api" % log4j2Version,
  "org.apache.logging.log4j" % "log4j-core" % log4j2Version,
  "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0"
)
