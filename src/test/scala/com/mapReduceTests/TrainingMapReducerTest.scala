package com.mapReduceTests

import com.trainingLLM.LLMEncoder
import com.utilities.Environment
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import java.nio.file.Files

class TrainingMapReducerTest extends BaseMrTest {

  "LLMEncoder MR" should " generate vector embeddings for the vocab it train" in {
    LLMEncoder.runJob(Environment.test)

    Files.list(directory).forEach { file =>
      Files.list(file).count() shouldBe (4)
    }
  }
}
