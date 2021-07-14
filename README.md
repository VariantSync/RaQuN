# RaQuN (Range Queries on N input models)

A scalable n-way model matching algorithm, which uses multi-dimensional search trees for efficiently finding suitable 
matching candidates through range queries. 

Repeating our experiments with the provided scripts in a Docker container should be easy and has only few requirements.
You can find instructions on how to build the Docker image in the INSTALL.md file, and instructions on how to run our
experiments in the README.md file.

## Project Structure

#### reported-results.zip
This archive contains all experimental results that we reported in our submission. Please unpack it into the 
root directory of the cloned project.
Your directory structure should then look as follows:

    ${RaQuN}/
      reported-results/
        argouml/
        NwM/
        PairwiseAsc/
        PairwiseDesc/
        RaQuN/
        RaQuN_k/

#### experimental_subjects
This folder contains archives with the csv-files that contain the input models used in our experiments. 

Unpack all zip-files inside the `experimental_subjects` and `experimental_subjects/argouml` directory to 
their current location.
The resulting file structure should look like this:

    ${RaQuN}/
      experimental_subjects/
        argouml/
          argouml_p001_s001.csv
          argouml_p001_s002.csv
          ...
          argouml_p095_s030.csv
        Apogames.csv 
        argouml.csv 
        bcms.csv
        bcs.csv
        example.csv 
        hospitals.csv 
        ppu.csv 
        ppu_statem.csv
        random.csv 
        randomLoose.csv 
        randomTight.csv
        warehouses.csv

#### lib
Deprecated jar-files containing third-party dependencies of our prototype. We switched to Maven, so they are no longer 
required and can be ignored. 
However, we included them in the repository in case Maven is for some reason not working as intended. In this case, 
you have to include them as external jars in your IDE. 

#### result_analysis_python
This is the python project containing the python scripts that were used to evaluate the experimental results. We recommend
to use [Pycharm](https://www.jetbrains.com/pycharm/) as IDE. You should open this directory as a project.

## Quick Reproduction
You can follow these few steps to run all experiments reported in the paper.

### Prerequisites
* Java: [JDK-11](https://www.oracle.com/technetwork/java/javase/downloads/index.html) or higher is installed on your machine

### Quick Setup
- Open a terminal and navigate to the project root
- Execute `java -jar RaQuN.jar`

## Getting Started in an IDE
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. 

### Prerequisites
Necessary software and how to install it:

* Java: [JDK-11](https://www.oracle.com/technetwork/java/javase/downloads/index.html) or higher 
* A suitable IDE: [IntelliJ](https://www.jetbrains.com/idea/download) OR [Eclipse](https://www.eclipse.org/downloads/) for Java developers with [Maven](https://www.eclipse.org/m2e/) Plugin
* Make sure that the IDE is set up to use Java 11 or higher. 


### Installing
How to get RaQuN running in ...

#### ... IntelliJ
##### Importing the Project 
If you are in the WELCOME screen of IntelliJ:

    > Open
    > Select the directory containing your clone of RaQuN.
    > Press Ok

If you already have another project open:

    > File
    > Open
    > Select the directory containing your clone of RaQuN.
    > Press Ok

##### Unit Tests
* Click on Maven on the right side of the IDE interface 

OR

* Hover with your mouse over the little boxes in the bottom-left corner of the IDE. > Wait until context menu opens > click Maven 


In the Maven menu you can now start a test cycle that runs all unit tests to validate the correct setup of the project 

    > Maven Menu
    > RaQuN
    > Lifecycle
    > right click on test
    > Run Maven Build

#### ... Eclipse 
##### Importing the Project 
Start Eclipse and open a workspace.
```
File > Open Projects from File System > Select the ${RaQuN} directory > Finish
```
##### Unit Tests
Open the Package Explorer:

    Right Click > Run As > JUnit Test

## Reproducing the Experiments

### In your IDE 
#### Prerequisites

* A JDK with version 11 or higher is registered in your IDE and set up for the project  

#### Steps 
You can follow these steps if you want to execute the experiments in your IDE 
* Unpack the experimental_subjects archives as described in 'Project Structure' above 
* Adjust the parameters in `ExperimentRunner` to your liking (see comments in the code).
* In your IDE: Right-click on the `ExperiementRunner.java` file > Run 'ExperimentRunner.main()'

### As JAR 
#### Prerequisites

* The JAVA_HOME variable of your OS is set to a JDK with version 11 or higher 

#### Steps
* Build the JAR file as described below in the 'Deployment' section
* Copy the RaQuN-1.0-jar-with-dependencies.jar to a directory of your choice
* Unpack the experimental_subjects archives as described in 'Project Structure' above 
* Copy the experimental_subjects located under ${RaQuN} to the directory where the JAR is located 
* Open a terminal and run 
```
java -jar RaQuN-1.0-jar-with-dependencies.jar 
```

## Evaluation of Result Files

The `ExperimentRunner` class saves all experimental results in a directory specified by its field `baseResultsDir`.
All results are grouped by the respective experimental setup, e.g., NwM running on PPU. 
The default location is: 

    ${RaQuN}/results       // When executing in your IDE
    or 
    ${LocationOfJARFiles}/results   // When executing as JAR

To reproduce our experimental evaluation creating the plots and tables shown in the paper, you have to run 
the `evaluation.py` script that can be found under: 

    ${RaQuN}/result_analysis_python/eval/evaluation.py

Therefore, open `${RaQuN}/result_analysis_python` as a project in a suitable Python-IDE, e.g., [Pycharm](https://www.jetbrains.com/pycharm/).
Run the following in your terminal, if you do not have matplotlib installed
- python -m pip install -U pip
- python -m pip install -U matplotlib

By default, the `evaluation.py` script is set up to run the evaluation of the reported results located in the 
`reported-results` directory. However, you can adjust the directory in line 9 to evaluate
results located elsewhere, for example the `results` directory if you ran the experiments yourself. 
To do this, change line 9 from

    data_directory = "./../../reported-results"
    
to

    data_directory = "./../../results"

If you use your own experimental results:
When configuring the `ExperimentRunner` earlier, you might have omitted some of the datasets or algorithms.
You have to adjust the `evaluation.py` the same by uncommenting all datasets and algorithms for which you didn't run experiments.
To do so, uncomment entries from the lists `datasets_part_1`, `datasets_part_2`, `normal_methods`, and `subset_methods` beginning from line 19.

The output of the script is saved under `${RaQuN}/result_analysis_python/eval_results/` and consists of the tables and plots as can be found in the paper.

To run the main evaluation script, right-click on the `evaluation.py` file in your IDE and then on `Run...`.

## Deployment

### Build

To deploy RaQuN as an executable JAR file, you have to run _Maven Install_ (similar to the instructions for _Maven Test_ above).
This will test and build the RaQuN prototype.
After the process is completed you can find two JAR files in the target folder 

    ${RaQuN}/target/RaQuN-1.0.jar
    ${RaQuN}/target/RaQuN-1.0-jar-with-dependencies.jar 

The `RaQuN-1.0.jar` only contains the binaries of RaQuN, without the libraries that RaQuN requires to run.
Therefore, you might want to use the `RaQuN-1.0-jar-with-dependencies.jar` file. 