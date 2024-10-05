package com.utilities

import com.config.ConfigLoader
import com.trainingLLM.Constants._
import com.utilities.Environment.{local, test, cloud}
import org.apache.hadoop.fs.Path
import org.apache.hadoop.mapred.{FileInputFormat, FileOutputFormat, JobConf}

import java.time.Instant

object JobConfigurationHelper {
//  var env = cloud;


  def getJobConfig(jobName : String, className: Class[_], env: Environment.Value) : JobConf = {
    val conf: JobConf = new JobConf(className)
    val outFile : String = "output-" + jobName + "-"+Instant.now().getEpochSecond.toString;
    conf.setJobName(jobName)
    env match {
      case Environment.local => {
        conf.set(HDFS_URL, ConfigLoader.getConfig("local.fs.defaultFS"))
        conf.set(MAX_SPLIT_SIZE_PARAM, ConfigLoader.getConfig("local.mapreduce.input.fileinputformat.split.maxsize"))
        conf.set(MAP_REDUCE_JOB_REDUCERS, ConfigLoader.getConfig("local.mapreduce.job.reduces"))
        FileInputFormat.setInputPaths(conf, new Path(ConfigLoader.getConfig("local.inputPath")))
        FileOutputFormat.setOutputPath(conf, new Path(ConfigLoader.getConfig("local.outputPath")+outFile))
      }
      case Environment.test  => {
        conf.set(HDFS_URL, ConfigLoader.getConfig("test.fs.defaultFS"))
        conf.set(MAX_SPLIT_SIZE_PARAM, ConfigLoader.getConfig("test.mapreduce.input.fileinputformat.split.maxsize"))
        conf.set(MAP_REDUCE_JOB_REDUCERS, ConfigLoader.getConfig("test.mapreduce.job.reduces"))
        FileInputFormat.setInputPaths(conf, new Path(ConfigLoader.getConfig("test.inputPath")))
        FileOutputFormat.setOutputPath(conf, new Path(ConfigLoader.getConfig("test.outputPath")+outFile))
      }
      case Environment.cloud => {
        conf.set(HDFS_URL, ConfigLoader.getConfig("cloud.fs.defaultFS"))
        conf.set(MAX_SPLIT_SIZE_PARAM, ConfigLoader.getConfig("cloud.mapreduce.input.fileinputformat.split.maxsize"))
        conf.set(MAP_REDUCE_JOB_REDUCERS, ConfigLoader.getConfig("cloud.mapreduce.job.reduces"))
        FileInputFormat.setInputPaths(conf, new Path(ConfigLoader.getConfig("cloud.inputPath")))
        FileOutputFormat.setOutputPath(conf, new Path(ConfigLoader.getConfig("cloud.outputPath")+outFile))
      }
      case _ => println("Not local, Test, or Cloud") // Catch-all case
    }
    conf
  }
}
