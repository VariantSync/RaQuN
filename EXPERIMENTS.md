# Experiments


## Running the Experiments Using Docker
**ATTENTION**
```
Make sure to delete all previously collected results by deleting the `./results` directory, as they will otherwise be 
counted as results of subsequent experiment executions. We only append results data, not overwrite it, to make it 
possible to run multiple instances of the same experiment in parallel.
```
```
All of the commands in this section are assumed to be executed in a terminal with working directory at RaQuN's project
root.
```

Repeating our experiments with the provided scripts in a Docker container should be easy and has only few requirements.
You can find information on the requirements and how to build the Docker image in the [REQUIREMENTS.md](REQUIREMENTS.md) and
[INSTALL.md](INSTALL.md) files.

### Running all Experiments
You can repeat the experiments exactly as presented in our paper. The following command will execute 30 runs of the experiments
for RQ1 and RQ2, and 1 run for the experiment of RQ3.
```shell
# Windows Command Prompt:
experiment.bat run
    
# Windows PowerShell:
.\experiment.bat run
    
# Linux
./experiment.sh run
```
`Expected Average Runtime for all experiments (@2.90GHz): 2460 hours or 102 days`

We provide instructions on how to parallelize the experiments for a shorter total runtime in the next sections.

### Running Specific Experiments
Due to the considerable runtime of running all experiments, we offer possibilities to run individual experiments and repetitions
of specific experiments in parallel.
You can run a single experiment repetition for any of the RQs (e.g., `experiment.bat RQ1` executes RQ1). If you want to
run multiple containers in parallel, you simply have to open a new terminal and start the experiment there as well.
```shell
# Windows Command Prompt:
experiment.bat (RQ1|RQ2|RQ3)
    
# Windows PowerShell:
.\experiment.bat (RQ1|RQ2|RQ3)
    
# Linux
./experiment.sh (RQ1|RQ2|RQ3)
```
#### Runtimes - All Matchers
`Expected Average Runtime for one Repetition of RQ1 (@2.90GHz): 4 hours` (Repeated 30 times for the paper)

`Expected Average Runtime for one Repetition of RQ2 (@2.90GHz): 8 hours` (Repeated 30 times for the paper)

`Expected Average Runtime for one Repetition of RQ3 (@2.90GHz): 2100 hours or 87 days` (Repeated 1 time for the paper)

You can reduce the required runtime significantly by excluding NwM from the experiments. You can find instructions on how
to configure the experiments under `Docker Experiment Configuration` below.

#### Runtimes - Without NwM
`Expected Average Runtime for one Repetition of RQ1 (@2.90GHz): 1 hour` (Repeated 30 times for the paper)

`Expected Average Runtime for one Repetition of RQ2 (@2.90GHz): 8 hours` (Repeated 30 times for the paper)

`Expected Average Runtime for one Repetition of RQ3 (@2.90GHz): 300 hours or 14 days` (Repeated 1 time for the paper)

Due to the large runtime of RQ3 we made it possible to run the experiments on individual subsets in parallel. There
are 30 subsets for each subset size. You can filter these subsets for the experiment by providing a `SUBSET_ID`. `SUBSET_ID`
has to be a natural number in the interval `[1, 30]` (e.g., `experiment.bat RQ3 1` will run RQ for all subsets with ID 1).
Hereby, you can start multiple Docker containers in
parallel.
```shell
# Windows Command Prompt:
experiment.bat RQ3 SUBSET_ID
    
# Windows PowerShell:
.\experiment.bat RQ3 SUBSET_ID
    
# Linux
./experiment.sh RQ3 SUBSET_ID
```
#### Runtimes - All Matchers
`Expected Average Runtime for one Repetition of RQ3 With a Specific SUBSET_ID (@2.90GHz): 70 hours`
(Repeated 1 time for each of the 30 valid `SUBSET_ID`)

#### Runtimes - Without NwM
`Expected Average Runtime for one Repetition of RQ3 (@2.90GHz): 10 hours` (Repeated 1 time for each of the 30 valid `SUBSET_ID`)

### Result Evaluation
You can run the result evaluation as done for our paper by calling the experiment script with `evaluate`. The
script will consider all data found under `./results`.
```shell
# Windows Command Prompt:
experiment.bat evaluate
    
# Windows PowerShell:
.\experiment.bat evaluate
    
# Linux
./experiment.sh evaluate
```
`Expected Average Runtime for all experiments (@2.90GHz): a few seconds`
The script will generate figures and tables similar to the ones presented in our paper. They are automatically saved to
`./results/eval-results`.

### Docker Experiment Configuration
By default, the properties used by Docker are configured to run the experiments as presented in our paper. We offer the
possibility to change the default configuration.
* Open the properties file which you want to adjust
    * [`full-experiments.properties`](docker-resources/full-experiments.properties) configures the experiment execution
      of `experiment.(bat|sh) run`
    * [`single-experiment.properties`](docker-resources/single-experiment.properties) configures specific experiment runs
      with `experiment.(bat|sh) (RQ1|RQ2|RQ3)`
* Change the properties to your liking
* Rebuild the docker image as described in [INSTALL.md](INSTALL.md)
* Delete old results in the `./results` folder
* Start the experiment as described above.

```
We ran the experiments in parallel on a compute server with 240 CPU cores and 1TB RAM. 
For RQ1 and RQ2, we set the number of repetitions to 2, for RQ3 to 1. Then, we executed the following sequential steps:
  - 15 parallel executions of RQ1 by calling 'experiment.(sh|bat) RQ1' in 15 different terminal sessions.
  - 15 parallel executions of RQ2 by calling 'experiment.(sh|bat) RQ2' in 15 different terminal sessions.
  - 30 parallel executions of RQ3 by calling 'experiment.(sh|bat) RQ3 SUBSET_ID' with the 30 different SUBSET_IDs in 
    different terminal sessions
The total runtime was about 3-4 days.
```

## Running the Experiments Without Docker
**ATTENTION**
```
Make sure to delete all previously collected results by deleting the `./results` directory, as they will otherwise be 
counted as results of subsequent experiment executions. We only append results data, not overwrite it, to make it 
possible to run multiple instances of the same experiment in parallel.
```
```
All of the commands in this section are assumed to be executed in a terminal with working directory at RaQuN's project
root.
```

You can find information on the requirements and how to build the required JAR files in the [REQUIREMENTS.md](REQUIREMENTS.md) and
[INSTALL.md](INSTALL.md) files.

### Running all Experiments
You can repeat the experiments exactly as presented in our paper. The following commands will execute 30 runs of the experiments
for RQ1 and RQ2, and 1 run for the experiment of RQ3.
```shell
java -jar target/RQ1Runner-jar-with-dependencies.jar docker-resources/full-experiments.properties
java -jar target/RQ2Runner-jar-with-dependencies.jar docker-resources/full-experiments.properties
java -jar target/RQ3Runner-jar-with-dependencies.jar docker-resources/full-experiments.properties
```
`Expected Average Runtime for all experiments (@2.90GHz): 2460 hours or 102 days`

### Running Specific Experiments
Due to the considerable runtime of running all experiments, we offer possibilities to run individual experiments and repetitions
of specific experiments in parallel.
You can run a single experiment repetition for any of the RQs. If you want to run multiple containers in parallel,
you simply have to open a new terminal and start the experiment there as well.
```shell
# RQ1:
java -jar target/RQ1Runner-jar-with-dependencies.jar docker-resources/single-experiment.properties
# RQ2:
java -jar target/RQ2Runner-jar-with-dependencies.jar docker-resources/single-experiment.properties
# RQ3:
java -jar target/RQ3Runner-jar-with-dependencies.jar docker-resources/single-experiment.properties
```
`Expected Average Runtime for one Repetition of RQ1 (@2.90GHz): 4 hours` (Repeated 30 times for the paper)

`Expected Average Runtime for one Repetition of RQ2 (@2.90GHz): 8 hours` (Repeated 30 times for the paper)

`Expected Average Runtime for one Repetition of RQ3 (@2.90GHz): 2100 hours or 87 days` (Repeated 1 time for the paper)

Due to the large runtime of RQ3 we made it possible to run the experiments on individual subsets in parallel. There
are 30 subsets for each subset size. You can filter these subsets for the experiment by providing a `SUBSET_ID`. `SUBSET_ID`
has to be a natural number in the interval `[1, 30]`.
Hereby, you can start multiple Docker containers in parallel.
```shell
java -jar target/RQ3Runner-jar-with-dependencies.jar docker-resources/single-experiment.properties SUBSET_ID
```
`Expected Average Runtime for one Repetition of RQ3 With a Specific SUBSET_ID (@2.90GHz): 70 hours`
(Repeated 1 time for each of the 30 valid `SUBSET_ID`)

### Result Evaluation
You can run the result evaluation as done for our paper by calling the python script. The
script will consider all data found under `./results`.

* Navigate to the script: `cd result_analysis_python`
* Start the evaluation:
  ```shell
  # Windows Command Prompt:
  python.exe evaluation.py
      
  # Windows PowerShell:
  python.exe evaluation.py
      
  # Linux
  python3.8 evaluation.py
  ```
`Expected Average Runtime (@2.90GHz): a few seconds`

The script will generate figures and tables similar to the ones presented in our paper. They are automatically saved to
`./results/eval-results`.

### Experiment Configuration
You can use the properties that are also used by Docker. By default, the properties are configured to run the
experiments as presented in our paper. We offer the possibility to change the default configuration.
* Open the properties file which you want to adjust
    * [`full-experiments.properties`](docker-resources/full-experiments.properties)
    * [`single-experiment.properties`](docker-resources/single-experiment.properties)
* Change the properties to your liking
* Delete old results in the `./results` folder
* Start the experiment as described above.

```
We ran the experiments in parallel on a compute server with 240 CPU cores and 1TB RAM. 
For RQ1 and RQ2, we set the number of repetitions to 2, for RQ3 to 1. Then, we executed the following sequential steps:
  - 15 parallel executions of RQ1 in 15 different terminal sessions.
  - 15 parallel executions of RQ2 in 15 different terminal sessions.
  - 30 parallel executions of RQ3 with the 30 different SUBSET_IDs in different terminal sessions
The total runtime was about 3-4 days.
```

## Using RaQuN as a Library in Your Own Projects
You can also use RaQuN as a Java Library in your own project. Simply add [`RaQuN.jar`](RaQuN.jar) as a dependency to
your project. Please refer to the documentation of your IDE or build system for instructions on how to add JARs as
dependencies. 

The [README](README.md) contains basic usage examples of how to use RaQuN in your project. In this section, we will 
provide pointers to the most important classes.

### RaQuN.java
[RaQuN.java](src/main/java/de/variantsync/matching/RaQuN.java) contains the implementation of RaQuN's algorithm as 
presented in the paper (Algorithm I). You can construct an instance of RaQuN by providing concrete instances for 
RaQuN`s configuration points:
```java
public RaQuN(IVectorization vectorizationFunction, IValidityConstraint validityConstraint, ISimilarityFunction similarityFunction, int nNearestNeighbors) {}
```
You can either implement the interfaces yourself, or you can use the implementations presented in our paper:
```
RaQuN raQuN = new RaQuN(new PropertyBasedVectorization(), new OneToOneValidity(), new WeightMetric());
```

