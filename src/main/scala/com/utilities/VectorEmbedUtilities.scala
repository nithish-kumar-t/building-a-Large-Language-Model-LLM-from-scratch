package com.utilities

import org.apache.hadoop.io.Text

import scala.collection.mutable.ArrayBuffer
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
    val it:Array[Text] = iterator.asScala.toArray;
    val count = it.length
    val dynamicArray = ArrayBuffer[Float]()

    it.foreach(item => {
      val currentArray = parseArray(item)

      if (dynamicArray.isEmpty) {
        dynamicArray.addAll(currentArray)
      } else {
        dynamicArray.indices.foreach(i => dynamicArray(i) += currentArray(i))
      }
    })
    dynamicArray.map(dim => dim/count).toArray
  }
}
