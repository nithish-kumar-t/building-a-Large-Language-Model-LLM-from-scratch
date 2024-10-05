
# building a Large-Language-Model (LLM) from scratch




### Author: Nithish Kumar
### Email: t.nithish136@gmail.com
##  Introduction
This project focuses on Implementing an LLM encoder using parallel distributed computations in the cloud, with Hadoop deploying it to AWS Elastic MapReduce (EMR).

[//]: # (This project focuses on creating a MapReduce program in Hadoop and deploying it to AWS Elastic MapReduce &#40;EMR&#41;. The primary objective is to process large graphs using Hadoop's distributed computing capabilities and analyze the differences between original and perturbed graphs. The project involves the use of the Guava library to generate induced subgraphs and a Breadth-First Search &#40;BFS&#41; algorithm to create shards that keep adjacent nodes together. This approach ensures that edges are not lost during the subgraph generation.)

### Environment
OS: Mac

### IDE: IntelliJ IDEA 2022.2.3 (Ultimate Edition)

### SCALA Version: 2.13.12

[//]: # (SBT Version: 1.8.3)

### Hadoop Version: 3.3.4

Running the test file
Test Files can be found under the directory src/test

````
sbt clean compile test
````
Running the project

1) Clone this repository
```
git clone git@github.com:nithish-kumar-t/building-a-Large-Language-Model-LLM-from-scratch.git
```


2) cd to the Project
```
cd building-a-Large-Language-Model-LLM-from-scratch
```
3) update the jars
```
sbt clean update
```

4) Create fat jat using assembly
```
sbt assembly
# This will create a fat Jar
```

5) we can then run UT's and FT's using below 
```
sbt test
```

6) Our application may contain multiple mains so, to check the correct main
```
➜building-a-Large-Language-Model-LLM-from-scratch git:(feature) ✗ sbt
[info] started sbt server
sbt:LLM-hw1-jar>
sbt:LLM-hw1-jar> show discoveredMainClasses
* com.trainingLLM.LLMEncoder com.trainingLLM.LLMEncoder
* com.trainingLLM.TokenizationJob
```

7) Create fat jat using assembly
```
hadoop jar <JarName> <Method to run> <environment>
eg: hadoop jar target/scala-2.13/llm-hw1.jar com.trainingLLM.TokenizationJob env=local
```


##  Project structure




