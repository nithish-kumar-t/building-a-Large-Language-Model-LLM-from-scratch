package com.config

import org.deeplearning4j.nn.conf.layers.{EmbeddingLayer, OutputLayer}
import org.deeplearning4j.nn.conf.{MultiLayerConfiguration, NeuralNetConfiguration}
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.lossfunctions.LossFunctions

object MultiLayerNetworkModel {
  val config: MultiLayerConfiguration = new NeuralNetConfiguration.Builder()
    .list()
    .layer(new EmbeddingLayer.Builder()
      .nIn(100_001) // +1 to include padding token if necessary
      .nOut(300) // Embedding dimension
      .activation(Activation.IDENTITY) // No activation function
      .build())
    .layer(new OutputLayer.Builder(LossFunctions.LossFunction.SPARSE_MCXENT) // Sparse cross-entropy for classification
      .nIn(100_000)
      .nOut(300) // Output is a probability distribution over the vocabulary
      .activation(Activation.SOFTMAX) // Softmax for next word prediction
      .build())
    .build()

  val model = new MultiLayerNetwork(config)
  model.init()

  def getModel: MultiLayerNetwork = {
    model
  }

}
