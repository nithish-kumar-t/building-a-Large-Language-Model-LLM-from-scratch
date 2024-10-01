package com.trainingLLM

import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.EncodingType
import org.apache.hadoop.conf.*
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.*
import org.apache.hadoop.mapred.*
import org.apache.hadoop.util.*

import java.io.IOException
import java.util
import scala.jdk.CollectionConverters.*

import com.trainingLLM.Constants.*;

import com.config.ConfigLoader;


object TokenizationJob:
  private class TokenizerMapper extends MapReduceBase with Mapper[LongWritable, Text, Text, IntWritable]:
    private final val one = new IntWritable(1)
    private val word = new Text()
    private val encoding = Encodings.newDefaultEncodingRegistry().getEncoding(EncodingType.CL100K_BASE);

    @throws[IOException]
    override def map(key: LongWritable, value: Text, output: OutputCollector[Text, IntWritable], reporter: Reporter): Unit =
      val tokens = encoding.encode(value.toString)
      tokens.asScala.foreach { token =>
        word.set(token.toString)
        output.collect(word, one)
      }

  private class IntSumReducer extends MapReduceBase with Reducer[Text, IntWritable, Text, IntWritable]:
    override def reduce(key: Text, values: util.Iterator[IntWritable], output: OutputCollector[Text, IntWritable], reporter: Reporter): Unit =
      val sum = values.asScala.map(_.get()).sum;
      output.collect(key,  new IntWritable(sum))

  @main def tokenizationMain(inputPath: String, outputPath: String): RunningJob =
    val conf: JobConf = new JobConf(this.getClass)
    conf.setJobName("WordCount")
    conf.set(HDFS_URL, ConfigLoader.getConfig(HADOOP_HDFS_URL))
    conf.set(MAX_SPLIT_SIZE_PARAM, ConfigLoader.getConfig(HADOOP_MAX_SPLIT_SIZE_PARAM))
    // Set the maximum split size
    conf.set(MAP_REDUCE_JOB_REDUCERS, ConfigLoader.getConfig(HADOOP_MAP_REDUCE_JOB_REDUCERS)) // 64 kb

    conf.setOutputKeyClass(classOf[Text])
    conf.setOutputValueClass(classOf[IntWritable])
    conf.setMapperClass(classOf[TokenizerMapper])
    conf.setCombinerClass(classOf[IntSumReducer])
    conf.setReducerClass(classOf[IntSumReducer])
    conf.setInputFormat(classOf[TextInputFormat])
    conf.setOutputFormat(classOf[TextOutputFormat[Text, IntWritable]])
    FileInputFormat.setInputPaths(conf, new Path(inputPath))
    FileOutputFormat.setOutputPath(conf, new Path(outputPath))
    JobClient.runJob(conf)