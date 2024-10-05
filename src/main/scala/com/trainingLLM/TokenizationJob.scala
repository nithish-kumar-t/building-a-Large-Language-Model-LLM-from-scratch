package com.trainingLLM

import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.EncodingType
import org.apache.hadoop.io._
import org.apache.hadoop.mapred._

import java.io.IOException
import java.util
import com.utilities.JobConfigurationHelper
import org.slf4j.LoggerFactory

import scala.jdk.CollectionConverters.IteratorHasAsScala

object TokenizationJob {
  private val logger = LoggerFactory.getLogger(getClass)
  private class TokenizerMapper extends MapReduceBase with Mapper[LongWritable, Text, Text, IntWritable] {
    private final val one = new IntWritable(1)
    private val word = new Text()
    private val encoding = Encodings.newDefaultEncodingRegistry().getEncoding(EncodingType.CL100K_BASE)

    @throws[IOException]
    override def map(key: LongWritable, value: Text, output: OutputCollector[Text, IntWritable], reporter: Reporter): Unit = {
      value.toString.toLowerCase().split("\\W+").filter(_.nonEmpty).foreach( token => {
        val encodedString =  encoding.encode(token.toLowerCase())
//        logger.info("Tokens Count " )
        word.set(token + "  " + encodedString.toString)
        output.collect(word, one)
      })
    }
  }

  private class IntSumReducer extends MapReduceBase with Reducer[Text, IntWritable, Text, IntWritable] {
    override def reduce(key: Text, values: util.Iterator[IntWritable], output: OutputCollector[Text, IntWritable], reporter: Reporter): Unit = {
      val sum = values.asScala.map(_.get()).sum
      output.collect(key, new IntWritable(sum))
    }
  }

  def main(args: Array[String]): Unit = {
    runJob()
  }

  def runJob(): RunningJob = {
    val conf: JobConf = JobConfigurationHelper.getJobConfig("WordCount", this.getClass)

    conf.setOutputKeyClass(classOf[Text])
    conf.setOutputValueClass(classOf[IntWritable])
    conf.setMapperClass(classOf[TokenizerMapper])
    conf.setCombinerClass(classOf[IntSumReducer])
    conf.setReducerClass(classOf[IntSumReducer])
    conf.setInputFormat(classOf[TextInputFormat])
    conf.setOutputFormat(classOf[TextOutputFormat[Text, IntWritable]])
    JobClient.runJob(conf)
  }
}