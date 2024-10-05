package com.mapReduceTests

import com.trainingLLM.TokenizationJob
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import java.nio.file.{Files, Paths}

class TokenizationMapReduceTest extends BaseMrTest{

  "The TokenizationJob MR" should "correctly generate words count" in {
    TokenizationJob.runJob()

    Files.list(directory).forEach { file =>
      Files.list(file).count() shouldBe (4)
    }
  }

}
