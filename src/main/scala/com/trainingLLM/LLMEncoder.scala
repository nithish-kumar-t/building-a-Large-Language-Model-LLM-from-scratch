package com.trainingLLM

import com.knuddels.jtokkit.Encodings
import com.knuddels.jtokkit.api.EncodingType
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io._
import org.apache.hadoop.mapred._

import java.io.{File, IOException}
import scala.collection.JavaConverters._
import com.trainingLLM.Constants._
import com.config.{ConfigLoader, MultiLayerNetworkModel}
import com.utilities.{TrainingDataGen, VectorEmbedUtilities}
import org.nd4j.linalg.factory.Nd4j
import org.slf4j.LoggerFactory


/**
 * MAP-REDUCE Implementation of training a huge tokenized text over a model  and generating it's vector embeddigs of each word.
 */
object LLMEncoder {
  private val logger = LoggerFactory.getLogger(getClass)

  /**
   * This Mapper implementation is used to convert sentences into tokens, and outputs word, with its token and Vector-embedding
   *
   */
  class TokenizerMapper extends MapReduceBase with Mapper[LongWritable, Text, Text, Text] {
    private val word = new Text()
    private val embedding = new Text()
    private val encoding = Encodings.newDefaultEncodingRegistry().getEncoding(EncodingType.CL100K_BASE)
    private val model = MultiLayerNetworkModel.getModel
    private val epochs: Int = ConfigLoader.getConfig(EPOCHS).toInt

    @throws[IOException]
    override def map(key: LongWritable, sentence: Text, output: OutputCollector[Text, Text], reporter: Reporter): Unit = {
      if (sentence.toString.isEmpty) {
        logger.debug("Current sentence is Invalid")
        return
      }

      //1. Using Jtokkit we are generating the tokens for a sentence.
      val tokens = encoding.encode(sentence.toString)
      logger.info("Tokens Count " + tokens.size())


      // 2. Generating input and output labels,
      val (features, labels) = TrainingDataGen.createInputTokensAndOutputLabels(tokens.asScala.toArray)
      val inputFeatures = Nd4j.create(features)
      val outputLabels = Nd4j.create(labels)


      // 3. Training the model using multiple epochs,
      Iterator.range(0, epochs).foreach { _ =>
        model.fit(inputFeatures, outputLabels)
      }

      val embeddings = model.getLayer(0).getParam("W")

      // 4. Storing the output in the form of Word and it's vector eg: "Hello [-1.000, 2.300, 3.60]"
      tokens.asScala.foreach(token => {
        word.set(encoding.decode(List(token).asJava) + "  " +token.toString)
        embedding.set(embeddings.getRow(token.longValue()).toStringFull)
        output.collect(word, embedding)
      })
    }
  }

  /**
   * This Reducer implementation takes, key value pair from the mapper, for similar words it is averaging the vector embeddings.
   *
   */
  class IntSumReducer extends MapReduceBase with Reducer[Text, Text, Text, Text] {
    override def reduce(key: Text, values: java.util.Iterator[Text], output: OutputCollector[Text, Text], reporter: Reporter): Unit = {
      val average = VectorEmbedUtilities.calculateAverage(values)
      output.collect(key, new Text(average.mkString("[", ", ", "]")))
    }
  }

  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      logger.error("Usage: TokenizationJob <input path> <output path>")
      System.exit(-1)
    }
    val inputPath = args(0)
    val outputPath = args(1)
    runJob(inputPath, outputPath)
  }

  def runJob(inputPath: String, outputPath: String): RunningJob = {
    if (!new File(inputPath).exists()) {
      logger.error("Given Input Path is not valid, there is no file exists with in the given path")
      System.exit(-1)
    }

    if (new File(outputPath).exists()) {
      logger.error("Given Output Path is already used")
      System.exit(-1)
    }

    val conf = new JobConf(this.getClass)
    conf.setJobName("WordCount")
    conf.set(HDFS_URL, ConfigLoader.getConfig(HADOOP_HDFS_URL))
    conf.set(MAX_SPLIT_SIZE_PARAM, ConfigLoader.getConfig(HADOOP_MAX_SPLIT_SIZE_PARAM))
    conf.set(MAP_REDUCE_JOB_REDUCERS, ConfigLoader.getConfig(HADOOP_MAP_REDUCE_JOB_REDUCERS))

    conf.setOutputKeyClass(classOf[Text])
    conf.setOutputValueClass(classOf[Text])
    conf.setMapperClass(classOf[TokenizerMapper])
    conf.setCombinerClass(classOf[IntSumReducer])
    conf.setReducerClass(classOf[IntSumReducer])
    conf.setInputFormat(classOf[TextInputFormat])
    conf.setOutputFormat(classOf[TextOutputFormat[Text, Text]])
    FileInputFormat.setInputPaths(conf, new Path(inputPath))
    FileOutputFormat.setOutputPath(conf, new Path(outputPath))
    JobClient.runJob(conf)
  }
}
