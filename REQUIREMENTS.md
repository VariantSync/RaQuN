# Requirements
This file specifies the hardware and software requirements that apply. Different software requirements apply, depending on 
whether you only want to repeat our experiments in a Docker container, or whether RaQuN is to be used as a library.

## Hardware Requirements
The following requirements apply for repeating the experiments presented in our paper. If you want to run the experiments
on your own datasets, more or less resources might be required.

#### CPU
Any CPU should work. However, your CPU directly influences the time required to run the experiments, how many executions can
be run in parallel, and the measured runtimes. We ran the experiments on a `Intel® Xeon® Processor E7-4880` at __2.90GHz__.
Be aware that running the experiments in parallel will negatively affect the validity of the runtime evaluation, if your 
processor does not have the necessary capabilities (number of (virtual) cores).

#### Memory (RAM)
Some experiments (e.g., matching ArgoUML) might require large amounts of working memory. __We advise__ to run the experiments
on a system with at least __16GB RAM__. If the experiments are to be
run in parallel, more resources will be required, depending on the number of runs.

#### Storage
The artifacts, experimental results, and docker objects might require up to __10GB of storage__. If the experiments are to be
run in parallel, more resources will be required, depending on the number of runs.



## Software Requirements - Running the Experiments with Docker
The following software requirements apply, if you plan to run the experiments presented in our paper __with__ Docker.

### OS
We provide batch scripts and bash scripts with parameterized Docker calls. You can use any OS supporting one of the script 
types. We tested the scripts on Windows 10, WSL2.0 with Ubuntu 20.04 LTS, and Manjaro Linux. If your OS supports
neither batch nor bash scripts, you can execute the Docker calls in the scripts manually by copying them to your terminal.

### Other Software
To run our experiments in Docker, you only require [Docker](https://docs.docker.com/get-docker/) to be installed and 
running on your system.
Please refer to the official [documentation](https://docs.docker.com/config/daemon/) if you run into any problems when 
starting the Docker Daemon.



## Software Requirements - Running the Experiments without Docker
The following software requirements apply **only** if you plan to run the experiments presented in our paper __without__ Docker.

```
We encourage you to use Docker though. It is easier to run, more robust, and 
you can also quickly run the experiments with a new setup by rebuilding the Docker image.
```

### OS
RaQuN is written in Java and the result evaluation is written in Python-3. Therefore, you can run the experiments on any OS on which you can 
install the required software (see below).

### Required Software
The following requirements apply, if you want to run and evaluate the experiments without Docker:
- [JDK-11](https://www.oracle.com/java/technologies/javase-downloads.html)
- [Maven](https://maven.apache.org/download.cgi), which is also integrated in most Java IDEs
- [Python-3.8](https://www.python.org/downloads/)
- [matplotlib](https://matplotlib.org/stable/users/installing.html)
- A tool for inflating archives (.zip). Examples are [7-ZIP](https://www.7-zip.org/) or [unzip](https://linux.die.net/man/1/unzip). Windows 10 and most Linux distribution already have a built-in archival tool.


