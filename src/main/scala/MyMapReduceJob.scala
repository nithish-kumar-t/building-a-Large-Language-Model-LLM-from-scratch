import com.config.ConfigLoader
import com.trainingLLM.Constants._
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.{IntWritable, LongWritable, Text}
import org.apache.hadoop.mapred._

import java.io.IOException
import java.util
import scala.jdk.CollectionConverters.IteratorHasAsScala

object MyMapReduceJob {
  class Map extends MapReduceBase with Mapper[LongWritable, Text, Text, IntWritable] {
    private final val one = new IntWritable(1)
    private val word = new Text()

    @throws[IOException]
    override def map(key: LongWritable, value: Text, output: OutputCollector[Text, IntWritable], reporter: Reporter): Unit = {
      val line: String = value.toString
      line.split(" ").foreach { token =>
        word.set(token)
        output.collect(word, one)
      }
    }
  }

  class Reduce extends MapReduceBase with Reducer[Text, IntWritable, Text, IntWritable] {
    override def reduce(key: Text, values: util.Iterator[IntWritable], output: OutputCollector[Text, IntWritable], reporter: Reporter): Unit = {
      val sum = values.asScala.reduce((valueOne, valueTwo) => new IntWritable(valueOne.get() + valueTwo.get()))
      output.collect(key, new IntWritable(sum.get()))
    }
  }

  def runMapReduce(inputPath: String, outputPath: String): RunningJob = {
    val conf: JobConf = new JobConf(this.getClass)
    conf.setJobName("WordCount")
    conf.set(HDFS_URL, ConfigLoader.getConfig(HADOOP_HDFS_URL))
    conf.set("mapreduce.job.maps", ConfigLoader.getConfig(HADOOP_MAX_SPLIT_SIZE_PARAM))
    conf.set("mapreduce.job.reduces", ConfigLoader.getConfig(HADOOP_MAP_REDUCE_JOB_REDUCERS))
    conf.setOutputKeyClass(classOf[Text])
    conf.setOutputValueClass(classOf[IntWritable])
    conf.setMapperClass(classOf[Map])
    conf.setCombinerClass(classOf[Reduce])
    conf.setReducerClass(classOf[Reduce])
    conf.setInputFormat(classOf[TextInputFormat])
    conf.setOutputFormat(classOf[TextOutputFormat[Text, IntWritable]])
    FileInputFormat.setInputPaths(conf, new Path(inputPath))
    FileOutputFormat.setOutputPath(conf, new Path(outputPath))
    JobClient.runJob(conf)
  }
}
