package com.mapReduceTests

import com.trainingLLM.LLMEncoder
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import java.nio.file.Files

class TrainingMapReducerTest extends BaseMrTest {

  "The LLMEncoder MR" should " generate vector embeddings for the vocab it train" in {
    LLMEncoder.runJob

    Files.list(directory).forEach { file =>
      Files.list(file).count() shouldBe (4)
    }
  }
}
