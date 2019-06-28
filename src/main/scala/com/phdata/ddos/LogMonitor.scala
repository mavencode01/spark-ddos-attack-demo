package com.phdata.ddos

import java.util.concurrent.TimeUnit

import com.phdata.ddos.entity.AccessLog
import com.phdata.ddos.utils.AccessLogParser
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.{OutputMode, Trigger}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{Row, SparkSession}

object LogMonitor {

  val DDOS_ATTACK_THRESHOLD = 4

  val logSchema = new StructType().add("record", "string")

  def main(args: Array[String]): Unit = {

    implicit val spark: SparkSession = SparkSession.builder()
                    .appName("log-attacks")
                    //.master("local[2]")
                    .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer" )
                    .config("spark.sql.streaming.checkpointLocation", "sample/checkpoint")
                    .config("spark.dynamicAllocation.enabled", "true")
                    .getOrCreate()

    LogMonitor.ddosAttack("sample/logs")

  }


  def ddosAttack(path: String)(implicit spark: SparkSession) = {

    import spark.implicits._

    val logsDF = spark
      .readStream
      .option("sep", "\n")
      .schema(logSchema )
      .csv(path).map(parseLog)
      .withWatermark("eventTime", "1 minutes")
      .groupBy($"eventTime", $"clientIpAddress").agg(count($"clientIpAddress").as("requestCount"))
      .filter($"requestCount" > DDOS_ATTACK_THRESHOLD)

    logsDF.printSchema()

    val query = logsDF.writeStream
      .format("parquet")
      .option("path", "sample/output/ddos_attacks.parquet")
      .trigger(Trigger.ProcessingTime(10, TimeUnit.SECONDS))
      .outputMode(OutputMode.Append())
      .start()


    query.awaitTermination()
  }

  def parseLog(row: Row): AccessLog = {
    AccessLogParser().parseRecord(row.getString(0)).get
  }

}
