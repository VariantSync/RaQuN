<img align="right" src="https://www.acm.org/binaries/content/gallery/acm/publications/artifact-review-v1_1-badges/artifacts_evaluated_reusable_v1_1.png" alt="ACM Artifacts Evaluated Reusable" width="114" height="113" />

![Maven](https://github.com/AlexanderSchultheiss/RaQuN/actions/workflows/maven.yml/badge.svg)
[![Javadoc](https://img.shields.io/badge/Javadocs-online-blue.svg?style=flat)](https://alexanderschultheiss.github.io/RaQuN/docs/)
[![GitHubPages](https://img.shields.io/badge/GitHub%20Pages-online-blue.svg?style=flat)](https://alexanderschultheiss.github.io/RaQuN/)
[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.5150388.svg)](https://doi.org/10.5281/zenodo.5150388)

# RaQuN (Range Queries on N input models)

RaQuN is a scalable n-way model matching algorithm, which uses multi-dimensional search trees for efficiently finding suitable 
matching candidates through range queries. This repository comprises the artifacts for our paper
_Scalable N-Way Model Matching Using Multi-Dimensional Search Trees_ which we presented at the 
International Conference on Model Driven Engineering Languages 2021 
([MODELS 2021](https://conf.researchr.org/home/models-2021)).
Authors:
[Alexander Schultheiß](https://www.informatik.hu-berlin.de/de/forschung/gebiete/mse/mitarb/alexander-schultheiss.html),
[Paul Maximilian Bittner](https://www.uni-ulm.de/in/sp/team/paul-maximilian-bittner/),
[Lars Grunske](https://www.informatik.hu-berlin.de/de/Members/lars-grunske),
[Thomas Thüm](https://www.uni-ulm.de/in/sp/team/thuem/),
[Timo Kehrer](https://www.informatik.hu-berlin.de/de/forschung/gebiete/mse/mitarb/kehrerti.html).

### Cite As
```bibtex
@inproceedings{SBG+:MODELS21,
	author = {Alexander Schulthei\ss{} and Paul Maximilian Bittner and Lars Grunske and Thomas Th{\"{u}}m and Timo Kehrer},
	title = {{Scalable N-Way Model Matching Using Multi-Dimensional Search Trees}},
	booktitle = {Proc.\ International Conference on Model Driven Engineering Languages and Systems (MODELS)},
	location = {Fukuoka, Japan},
	publisher = {ACM/IEEE},
	year = 2021,
	month = OCT,
	keywords = {model-driven engineering, n-way model matching, clone-and-own development, software product lines, multiview integration, variability mining}
}
```

### Contact
Please contact Alexander Schultheiß if you have any questions:
* Mail: [AlexanderSchultheiss@pm.me](AlexanderSchultheiss@pm.me)
* Discord: AlexS#1561

## Obtaining the Artifacts
Clone the repository to a location of your choice using [git](https://git-scm.com/):
  ```
  git clone https://github.com/AlexanderSchultheiss/RaQuN.git
  ```

## Project Structure
The project contains a number of files and folders with interesting content.

* [`docker-resources`](docker-resources) contains the script and property files used by the Docker containers.
  * `docker-resources/full-experiments.properties` configures the experiments as presented in our paper. 
  * `docker-resources/single-experiment.properties` configures running a single repetition of specific experiments.
  * `docker-resources/quick-validation.properties` configures a quick experiment for validating the functionality.
* [`docs`](docs) contains the Javadocs for the project, which you can also find [here](https://alexanderschultheiss.github.io/RaQuN/docs/). 
* [`experimental_subjects`](experimental_subjects) contains the archives with the csv-files describing the input models used in our experiments.
* [`result_analysis_python`](result_analysis_python) contains the Python scripts which we used to evaluate the experiments' results
and generate the plots and tables for our paper.
* [`src`](src/main/java/de/variantsync/matching) contains the source files used to run the experiments, and the source files
of the different matchers that we evaluated.
  * [`experiments`](src/main/java/de/variantsync/matching/experiments) contains all sources related to running the experiments written by us.
  * [`nwm`](src/main/java/de/variantsync/matching/nwm) contains the sources of the NwM prototype implementation written by Rubin and Chechik and slightly adjusted by us.
  * [`pairwise`](src/main/java/de/variantsync/matching/pairwise) contains a wrapper written by us for Rubin and Chechik's implementation of a pairwise matcher. 
  * [`raqun`](src/main/java/de/variantsync/matching/raqun) contains RaQuN's implementation written by us.
* [`EXPERIMENTS.md`](EXPERIMENTS.md) contains detailed instructions on how to run and configure experiments with and without Docker. You can find basic instructions in the sections below.
* [`INSTALL.md`](INSTALL.md) contains detailed instructions on how to prepare the artifacts for running on your system.
* [`LICENSE.md`](LICENSE.md) contains licensing information.
* [`REQUIREMENTS.md`](REQUIREMENTS.md) contains the requirements for installing and running the artifacts on your system.
* [`STATUS.md`](STATUS.md) specifies the [ACM badges](https://www.acm.org/publications/policies/artifact-review-and-badging-current)
  which we apply for.
* [`build-docker-image.bat`](build-docker-image.bat)|[`build-docker-image.sh`](build-docker-image.sh) is a script that builds the Docker image with which the experiments presented in our paper can be executed.
* [`experiment.bat`](experiment.bat)|[`experiment.sh`](experiment.sh)is a script for running the experiments in a Docker container. See the `Running the Experiments` section below.
* `reported-results.zip` is an archive with the raw result data reported in our paper.
* [`stop-all-experiments.bat`](stop-all-experiments.bat)|[`stop-all-experiments.sh`](stop-all-experiments.sh) is a script that will stop all Docker containers currently running experiments.

## Requirements and Installation

___This is a quickstart guide. For a detailed step-by-step guide please refer to [REQUIREMENTS.md](REQUIREMENTS.md) and 
[INSTALL.md](INSTALL.md). There you can also find the specific Docker commands that are executed by the scripts below.___

### Setup Instructions
* Install [Docker](https://docs.docker.com/get-docker/) on your system and start the [Docker Daemon](https://docs.docker.com/config/daemon/).
* Open a terminal and navigate to the project's root directory
* Build the docker image by calling the build script corresponding to your OS
  ```shell
  # Windows:
  build-docker-image.bat
  # Linux | MacOS:
  build-docker-image.sh
  ```
* You can validate the installation by calling the validation corresponding to your OS. The validation should take about
  `30 minutes` depending on your system.
  ```shell
  # Windows:
  experiment.bat validate
  # Linux | MacOS:
  experiment.sh validate
  ```
  The script will generate figures and tables similar to the ones presented in our paper. They are automatically saved to
  `./results/eval-results`.

## Running the Experiments Using Docker

___This is a quickstart guide. For a detailed step-by-step guide and instructions for 
running the experiments without Docker please refer to [EXPERIMENTS.md](EXPERIMENTS.md). There you can also find the specific Docker commands that are executed by the scripts below.___

**ATTENTION**
```
! Before running or re-running any experiments:
! Make sure to delete all previously collected results by deleting the `./results` directory, as they will otherwise be 
! counted as results of parallel experiment executions. We only append results data, not overwrite it, to make it 
! possible to run multiple instances of the same experiment in parallel.
```

* All of the commands in this section are assumed to be executed in a terminal with working directory at RaQuN's project
root.
* You can stop the execution of any experiment by running the following command in another terminal:
  ```shell
  # Windows Command Prompt:
  stop-all-experiments.bat
  # Windows PowerShell:
  .\stop-all-experiments.bat
  # Linux | MacOS
  ./stop-all-experiments.sh
  ```
Stopping the execution may take a moment.

[comment]: <> (* You can find more detailed information on how to run and configure the experiments with and without Docker in [EXPERIMENTS.md]&#40;EXPERIMENTS.md&#41;.)

### Running all Experiments
You can repeat the experiments exactly as presented in our paper. The following command will execute 30 runs of the experiments
for RQ1 and RQ2, and 1 run for the experiment of RQ3. 
```shell
# Windows Command Prompt:
experiment.bat run
# Windows PowerShell:
.\experiment.bat run
# Linux | MacOS
./experiment.sh run
```
```
Expected Average Runtime for all experiments (@2.90GHz): 2460 hours or 102 days.

We provide instructions on how to parallelize the experiments for a shorter total runtime in the next sections.
```

### Running Specific Experiments
Due to the considerable runtime of running all experiments in a single container, we offer possibilities to run 
individual experiments and repetitions of specific experiments in parallel.
You can run a single experiment repetition for any of the RQs (e.g., `experiment.bat RQ1` executes RQ1). If you want to 
run multiple containers in parallel, you simply have to open a new terminal and start the experiment there as well. 
```shell
# Windows Command Prompt:
experiment.bat (RQ1|RQ2|RQ3)  
# Windows PowerShell:
.\experiment.bat (RQ1|RQ2|RQ3)
# Linux | MacOS
./experiment.sh (RQ1|RQ2|RQ3)
```
#### Runtime
`Expected Average Runtime for one Repetition of RQ1 (@2.90GHz): 4 hours` (Repeated 30 times for the paper)

`Expected Average Runtime for one Repetition of RQ2 (@2.90GHz): 8 hours` (Repeated 30 times for the paper)

`Expected Average Runtime for one Repetition of RQ3 (@2.90GHz): 2100 hours or 87 days` (Repeated 1 time for the paper)

#### Running RQ3 on Specific ArgoUML Subsets
Due to the large runtime of RQ3, we made it possible to run the experiments on individual subsets in parallel. There
are 30 subsets for each subset size. You can filter these subsets for the experiment by providing a `SUBSET_ID`. `SUBSET_ID`
has to be a natural number in the interval `[1, 30]` (e.g., `experiment.bat RQ3 1` will run RQ for all subsets with ID 1).
Hereby, you can start multiple Docker containers in 
parallel.
```shell
# Windows Command Prompt:
experiment.bat RQ3 SUBSET_ID  
# Windows PowerShell:
.\experiment.bat RQ3 SUBSET_ID  
# Linux | MacOS
./experiment.sh RQ3 SUBSET_ID
```

#### Runtime
`Expected Average Runtime for one Repetition of RQ3 With a Specific SUBSET_ID (@2.90GHz): 70 hours` 
(Repeated 1 time for each of the 30 valid `SUBSET_ID`)

```
We ran the experiments in parallel on a compute server with 240 CPU cores (2.90GHz) and 1TB RAM. 
For RQ1 and RQ2, we set the number of repetitions to 2, for RQ3 to 1. Then, we executed the following sequential steps:
  - 15 parallel executions of RQ1 by calling 'experiment.(sh|bat) RQ1' in 15 different terminal sessions.
  - 15 parallel executions of RQ2 by calling 'experiment.(sh|bat) RQ2' in 15 different terminal sessions.
  - 30 parallel executions of RQ3 by calling 'experiment.(sh|bat) RQ3 SUBSET_ID' with the 30 different SUBSET_IDs in 
    different terminal sessions
The total runtime was about 3-4 days.
```

### Result Evaluation
You can run the result evaluation by calling the experiment script with `evaluate`. The
script will consider all data found under `./results`.
```shell
# Windows Command Prompt:
experiment.bat evaluate
# Windows PowerShell:
.\experiment.bat evaluate  
# Linux | MacOS
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

__You can find instructions on how to run the experiments on your own datasets in [EXPERIMENTS.md](EXPERIMENTS.md).__

### Clean-Up
The more experiments you run, the more space will be required by Docker. The easiest way to clean up all Docker images and
containers afterwards is to run the following command in your terminal. Note that this will remove all other containers and images
not related to RaQuN as well:
```
docker system prune -a
```
Please refer to the official documentation on how to remove specific [images](https://docs.docker.com/engine/reference/commandline/image_rm/) and [containers](https://docs.docker.com/engine/reference/commandline/container_rm/) from your system.

## Using RaQuN as a Library in Your Own Projects
You can also use RaQuN as a Java Library in your own project. To do so, you will have to prepare your system the same way
as for running the experiments without Docker. Please refer to the [REQUIREMENTS.md](REQUIREMENTS.md) and [INSTALL.md](INSTALL.md)
for instructions on how to do so. Once prepared, you can build a JAR file containing RaQuN using Maven:
* Execute the following a terminal with working directory in the project's root folder:
  ````shell
  mvn package 
  ````
* You can then find the JAR file containing RaQuN and all its dependencies as a library under `./target/RaQuN-jar-with-dependencies.jar`
* Please refer to the documentation of your IDE or build system for instructions on how to add JARs as 
dependencies. 

### Examples 
#### Matching a Dataset Stored in CSV Format
The following presents a simple example on how to compute a matching for a dataset with RaQuN:
```java
import java.util.Set;
import de.variantsync.matching.raqun.RaQuN;
import de.variantsync.matching.raqun.data.RMatch;
import de.variantsync.matching.raqun.data.RDataset;
import de.variantsync.matching.raqun.similarity.WeightMetric;
import de.variantsync.matching.raqun.validity.OneToOneValidity;
import de.variantsync.matching.raqun.vectorization.PropertyBasedVectorization;

import java.nio.file.Paths;

public class Main {
    public static void main(String... args) {
        var dataset = new RDataset("MyDataset");
        dataset.loadFileContent(Paths.get("path/to/MyDataset.csv"));
        
        RaQuN raQuN = new RaQuN(new PropertyBasedVectorization(), new OneToOneValidity(), new WeightMetric());
        Set<RMatch> matching = raQuN.match(dataset.getModels());
    }
}
```

#### Matching a Dataset Created with Raw Data
The following presents a simple example on how you can initialize the models of a dataset in code, and then match them 
with RaQuN:
```java
import java.util.Set;
import de.variantsync.matching.raqun.RaQuN;
import de.variantsync.matching.raqun.data.RElement;
import de.variantsync.matching.raqun.data.RMatch;
import de.variantsync.matching.raqun.data.RModel;
import de.variantsync.matching.raqun.similarity.WeightMetric;
import de.variantsync.matching.raqun.validity.OneToOneValidity;
import de.variantsync.matching.raqun.vectorization.PropertyBasedVectorization;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String... args) {
        Set<RModel> models = new HashSet<>();
        
        RModel modelA = new RModel("A");
        modelA.addElement(new RElement("A", "X0","Element-1", Collections.singletonList("prop1, prop2")));
        modelA.addElement(new RElement("A", "X1","Element-2", Collections.singletonList("prop3, prop4")));
        models.add(modelA);

        RModel modelB = new RModel("B");
        modelB.addElement(new RElement("B", "X0","Element-1", Collections.singletonList("prop1, prop2, prop3")));
        modelB.addElement(new RElement("B", "X1","Element-2", Collections.singletonList("prop3, prop4")));
        models.add(modelB);
        
        RaQuN raQuN = new RaQuN(new PropertyBasedVectorization(), new OneToOneValidity(), new WeightMetric());
        Set<RMatch> matching = raQuN.match(models);
    }
}
```
