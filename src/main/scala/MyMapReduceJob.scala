import org.apache.hadoop.conf.*
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.*
//import org.apache.hadoop.mapred.*
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer}
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat

import scala.jdk.CollectionConverters.*

object MyMapReduceJob {

  class WordCountMapper extends Mapper[Object, Text, Text, IntWritable] {
    private val word = new Text()
    private val one = new IntWritable(1)

    override def map(key: Object, value: Text, context: Context): Unit = {
      val words = value.toString.split("\\s+")
      for (w <- words) {
        word.set(w)
        context.write(word, one)
      }
    }
  }
  
  class WordCountReducer extends Reducer[Text, IntWritable, Text, IntWritable] {
    override def reduce(key: Text, values: java.lang.Iterable[IntWritable], context: Context): Unit = {
      val sum = values.asScala.map(_.get()).sum
      context.write(key, new IntWritable(sum))
    }
  }

  def main(args: Array[String]): Unit = {
    val conf = new Configuration()
    val job = Job.getInstance(conf, "Word Count")

    job.setJarByClass(this.getClass)
    job.setMapperClass(classOf[WordCountMapper])
    job.setReducerClass(classOf[WordCountReducer])

    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[IntWritable])

    FileInputFormat.addInputPath(job, new Path(args(0)))
    FileOutputFormat.setOutputPath(job, new Path(args(1)))

    System.exit(if (job.waitForCompletion(true)) 0 else 1)
  }
}


//import org.apache.hadoop.fs.Path
//import org.apache.hadoop.conf.*
//import org.apache.hadoop.io.*
//import org.apache.hadoop.util.*
//import org.apache.hadoop.mapred.*
//
//import java.io.IOException
//import java.util
//import scala.jdk.CollectionConverters.*
//
//
//object MapReduceProgram:
//  class Map extends MapReduceBase with Mapper[LongWritable, Text, Text, IntWritable]:
//    private final val one = new IntWritable(1)
//    private val word = new Text()
//
//    @throws[IOException]
//    override def map(key: LongWritable, value: Text, output: OutputCollector[Text, IntWritable], reporter: Reporter): Unit =
//      val line: String = value.toString
//      line.split(" ").foreach { token =>
//        word.set(token)
//        output.collect(word, one)
//      }
//
//  class Reduce extends MapReduceBase with Reducer[Text, IntWritable, Text, IntWritable]:
//    override def reduce(key: Text, values: util.Iterator[IntWritable], output: OutputCollector[Text, IntWritable], reporter: Reporter): Unit =
//      val sum = values.asScala.reduce((valueOne, valueTwo) => new IntWritable(valueOne.get() + valueTwo.get()))
//      output.collect(key,  new IntWritable(sum.get()))
//
//  @main def runMapReduce(inputPath: String, outputPath: String) =
//    val conf: JobConf = new JobConf(this.getClass)
//    conf.setJobName("WordCount")
//    conf.set("fs.defaultFS", "local")
//    conf.set("mapreduce.job.maps", "1")
//    conf.set("mapreduce.job.reduces", "1")
//    conf.setOutputKeyClass(classOf[Text])
//    conf.setOutputValueClass(classOf[IntWritable])
//    conf.setMapperClass(classOf[Map])
//    conf.setCombinerClass(classOf[Reduce])
//    conf.setReducerClass(classOf[Reduce])
//    conf.setInputFormat(classOf[TextInputFormat])
//    conf.setOutputFormat(classOf[TextOutputFormat[Text, IntWritable]])
//    FileInputFormat.setInputPaths(conf, new Path(inputPath))
//    FileOutputFormat.setOutputPath(conf, new Path(outputPath))
//    JobClient.runJob(conf)
