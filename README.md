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

## Obtaining the Artifacts
You can download the project via [Zenodo](TODO) or clone the [git](https://git-scm.com/) repository from [GitHub](https://github.com/AlexanderSchultheiss/RaQuN).

### Zenodo
* Download the [archive](TODO)
* Extract the archive to a location of your choice

### GitHub
* Clone the repository to a location of your choice
  ```
  git clone https://github.com/AlexanderSchultheiss/RaQuN.git
  ```

## Project Structure
The project contains a number of files and folders with interesting content.

* [`docker-resources`](docker-resources) contains the script and property files used by the Docker containers.
  * `docker-resources/experiment.properties` configures the experiments as presented in our paper. 
  * `docker-resources/quick-validation.properties` configures a quick experiment for validating the Docker image's functionality.
* [`experimental_subjects`](experimental_subjects) contains the archives with the csv-files describing the input models used in our experiments.
* [`result_analysis_python`](result_analysis_python) contains the Python scripts which we used to evaluate the experiments' results
and generate the plots and tables for our paper.
* [`src`](src/main/java/de/variantsync/matching) contains the source files used to run the experiments, and the source files
of the different matchers that we evaluated.
  * [`experiments`](src/main/java/de/variantsync/matching/experiments) contains all sources related to running the experiments written by us.
  * [`nwm`](src/main/java/de/variantsync/matching/nwm) contains the sources of the NwM prototype implementation written by Rubin and Chechik and slightly adjusted by us.
  * [`pairwise`](src/main/java/de/variantsync/matching/pairwise) contains a wrapper written by us for Rubin and Chechik's implementation of a pairwise matcher. 
  * [`raqun`](src/main/java/de/variantsync/matching/raqun) contains RaQuN's implementation written by us.
* [`INSTALL.md`](INSTALL.md) contains detailed instructions on how to prepare the artifacts for running on your system.
* [`LICENSE.md`](LICENSE.md) contains licensing information.
* [`REQUIREMENTS.md`](REQUIREMENTS.md) contains the requirements for installing and running the artifacts on your system.
* [`RaQuN.jar`](RaQuN.jar) A pre-build library of RaQuN which you can add as a dependency to your own projects.
* [`STATUS.md`](STATUS.md) specifies the [ACM badges](https://www.acm.org/publications/policies/artifact-review-and-badging-current)
  which we apply for.
* `build-docker-image.(bat|sh)` is a script that builds the Docker image with which the experiments presented in our paper can be executed.
* `experiments.(bat|sh)` is a script for running the experiments in a Docker container. See the `Running the Experiments` section below.
* `reported-results.zip` is an archive with the raw result data reported in our paper.
* `stop-all-experiments.(bat|sh)` is a script that will stop all Docker containers currently running experiments.

## Requirements and Installation
If you are familiar with the terminal running on your system and Docker, you can follow the instructions in this sections
to quickly get the experiments running. If you have trouble during the execution of the steps
described here, or if you plan on running the experiments without Docker, please refer to the detailed steps described in the [REQUIREMENTS.md](REQUIREMENTS.md) and 
[INSTALL.md](INSTALL.md) files.

* Install [Docker](https://docs.docker.com/get-docker/) on your system and start the [Docker Daemon](https://docs.docker.com/config/daemon/).
* Open a terminal and navigate to the project's root directory
* Build the docker image by calling the build script corresponding to your OS
  ```
  Windows:
    build-docker-image.bat
  Linux:
    build-docker-image.sh
  ```
* You can validate the installation by calling the validation corresponding to your OS. The validation should take about
  `30 minutes` depending on your system.
  ```
  Windows:
    experiment.bat validate
  Linux:
    experiment.sh validate
  ```

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
```
  Windows Command Prompt:
    experiment.bat run
    
  Windows PowerShell:
    .\experiment.bat run
    
  Linux
    ./experiment.sh run
```
`Expected Average Runtime for all experiments (@2.90GHz): 2460 hours or 102 days` 

We provide instructions on how to parallelize the experiments for a shorter total runtime in the next sections.

### Running Specific Experiments
Due to the considerable runtime of running all experiments, we offer possibilities to run individual experiments and repetitions
of specific experiments in parallel.
You can run a single experiment repetition for any of the RQs (e.g., `experiment.bat RQ1` executes RQ1). If you want to 
run multiple containers in parallel, you simply have to open a new terminal and start the experiment there as well. 
```
  Windows Command Prompt:
    experiment.bat (RQ1|RQ2|RQ3)
    
  Windows PowerShell:
    .\experiment.bat (RQ1|RQ2|RQ3)
    
  Linux
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
```
  Windows Command Prompt:
    experiment.bat RQ3 SUBSET_ID
    
  Windows PowerShell:
    .\experiment.bat RQ3 SUBSET_ID
    
  Linux
    ./experiment.sh RQ3 SUBSET_ID
```
#### Runtimes - All Matchers 
`Expected Average Runtime for one Repetition of RQ3 With a Specific SUBSET_ID (@2.90GHz): 70 hours` 
(Repeated 1 time for each of the 30 valid `SUBSET_ID`)

#### Runtimes - Without NwM
`Expected Average Runtime for one Repetition of RQ3 (@2.90GHz): 10 hours` (Repeated 1 time for each of the 30 valid `SUBSET_ID`)

### Docker Experiment Configuration
By default, the properties used by Docker are configured to run the experiments as presented in our paper. We offer the 
possibility to change the default configuration. 
* Open the properties file which you want to adjust
  * [`full-experiment.properties`](docker-resources/full-experiments.properties) configures the experiment execution 
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
```
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
```
RQ1:
  java -jar target/RQ1Runner-jar-with-dependencies.jar docker-resources/single-experiment.properties
RQ2:
  java -jar target/RQ2Runner-jar-with-dependencies.jar docker-resources/single-experiment.properties
RQ3:
  java -jar target/RQ3Runner-jar-with-dependencies.jar docker-resources/single-experiment.properties
```
`Expected Average Runtime for one Repetition of RQ1 (@2.90GHz): 4 hours` (Repeated 30 times for the paper)

`Expected Average Runtime for one Repetition of RQ2 (@2.90GHz): 8 hours` (Repeated 30 times for the paper)

`Expected Average Runtime for one Repetition of RQ3 (@2.90GHz): 2100 hours or 87 days` (Repeated 1 time for the paper)

Due to the large runtime of RQ3 we made it possible to run the experiments on individual subsets in parallel. There
are 30 subsets for each subset size. You can filter these subsets for the experiment by providing a `SUBSET_ID`. `SUBSET_ID`
has to be a natural number in the interval `[1, 30]`.
Hereby, you can start multiple Docker containers in parallel.
```
java -jar target/RQ3Runner-jar-with-dependencies.jar docker-resources/single-experiment.properties SUBSET_ID
```
`Expected Average Runtime for one Repetition of RQ3 With a Specific SUBSET_ID (@2.90GHz): 70 hours`
(Repeated 1 time for each of the 30 valid `SUBSET_ID`)

### Experiment Configuration
You can use the properties that are also used by Docker. By default, the properties are configured to run the 
experiments as presented in our paper. We offer the possibility to change the default configuration.
* Open the properties file which you want to adjust
  * [`full-experiment.properties`](docker-resources/full-experiments.properties) 
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

## Running the Experiments on New Datasets
It is possible to run the experiments on new element-property datasets that are stored in a `csv` file. The experiment
runner expects the `csv` to have to following format:
* The file contains all models of the dataset
* Each line in the file corresponds to exactly one model element
* Each line adheres to the following format
  * `MODEL_ID` `,` `ELEMENT_UUID` `,` `ELEMENT_NAME` `,` `PROPERTY_1` `;` `PROPERTY_2` `;` `...` `;` `PROPERTY_M`
  * Examples 
    * `1,2,BookingCalendar,n_bookingCalendar_1;equipment_1;adminAssistant_1;procedure_1`
    * `2,27,Bed,n_bed_1`
    * `2,46,Room,n_room_1;displayScanner_1;bed_1;stationary_1;bed_2`
* `,` and `;` are delimeters and must not be used in the values
* If an element has multiple properties with exactly the same values, the properties of all elements in the dataset should
be enumerated by appending a number to the property as seen in the examples above.
  
Do the following to use the dataset in an experiment:
* Save the dataset's csv file to the [`experiment_subjects`](experimental_subjects) directory, next to the other dataset files
* Open the experiment properties in a text editor
  * [`full-experiment.properties`](docker-resources/full-experiments.properties) configures the experiment execution
    of `experiment.(bat|sh) run`
  * [`single-experiment.properties`](docker-resources/single-experiment.properties) configures specific experiment runs
    with `experiment.(bat|sh) (RQ1|RQ2|RQ3)`
* Add the dataset's filename without the `.csv` extension to the list of datasets for RQ1 OR RQ2 
  * `experiments.rq1.datasets = YOUR_DATASET,hospitals, ...`
  * `experiments.rq2.datasets = YOUR_DATASET,ppu,bcms`
* If you are using Docker, rebuild the docker image as described in [INSTALL.md](INSTALL.md)
* Run the experiments as described above

## Using RaQuN as a Library in Your Own Projects
You can also use RaQuN as a Java Library in your own project. Simply add [`RaQuN.jar`](RaQuN.jar) as a dependency to 
your project. Please refer to the documentation of your IDE or build system for instructions on how to add JARs as 
dependencies. 

### Matching a Dataset Stored in CSV Format
The following presents a simple example on how to compute a matching for a dataset with RaQuN:
```
import de.variantsync.matching.raqun.RaQuN;
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
        Set<RMatch> matching = raQuN.match(models);
    }
}
```

### Matching a Dataset Created with Raw Data
The following presents a simple example on how you can initialize the models of a dataset in code, and then match them 
with RaQuN:
```
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
