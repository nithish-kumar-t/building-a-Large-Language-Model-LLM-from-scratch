package com.utilities

import org.apache.hadoop.io.Text

import scala.collection.mutable.ArrayBuffer
//import scala.collection.JavaConverters._
import scala.jdk.CollectionConverters._

object VectorEmbedUtilities {
  private def parseArray(text: Text): Array[Float] = {
    // Convert Text to String and parse it to Array[Float]
    text.toString
      .replace("[", "") // Remove the opening bracket
      .replace("]", "") // Remove the closing bracket
      .split(",") // Split by comma
      .map(_.trim.toFloat) // Trim whitespace and convert to Float
  }

  def calculateAverage(iterator: java.util.Iterator[Text]): Array[Float] = {
    // Initialize variables to store sum and count of arrays
    val dynamicArray = ArrayBuffer[Float]()
    val it:Array[Text] = iterator.asScala.toArray;
    val count = it.length

    // iterator.asScala.reduce((a, b) => parseArray(a).zip(parseArray(b)).map { case (e1, e2) => e1 + e2 })
    it.foreach(item => {
      val currentArray = parseArray(item)

      // Initialize sumArray if it's the first time
      //values.asScala.reduce((valueOne, valueTwo) => new IntWritable(valueOne.get() + valueTwo.get()))
      if (dynamicArray.isEmpty) {
        dynamicArray.addAll(currentArray)
//        currentArray.foreach(ele => dynamicArray.addOne(ele))
      }

      // Add currentArray to sumArray element-wise
      dynamicArray.indices.foreach(i => dynamicArray(i) + currentArray(i))
    })
    // Calculate the average for each element
    dynamicArray.map(dim => dim/count).toArray
  }
}
