# TODO
- Installation instructions
  - Illustration of a very basic usage example or a method to test the installation
  - What output to expect that confirms that the code is installed and working
  - What output to expect that confirms that the code is doing something interesting and useful
    
# Installation
Please refer to REQUIREMENTS.md for the software requirements of different setups.

## Installation- Running the Experiments with Docker
The following installation instructions apply, if you plan to run the experiments presented in our paper __with__ Docker.

### Installation Instructions
In the following, we describe how to build the Docker image and run the experiments in Docker containers.

* First, open a suitable terminal.
```
Windows Command Prompt: 
 - Press 'Windows Key + R' on your keyboard
 - Type in 'cmd' 
 - Click 'OK' or press 'Enter' on your keyboard
 
Windows PowerShell:
 - Open the search bar (Default: 'Windows Key') and search for 'PowerShell'
 - Start the PowerShell
 
Linux:
 - Press 'ctrl + alt + T' on your keyboard
```

* Navigate to RaQuN's root directory

```
cd PATH_TO_DIRECTORY/RaQuN
```

* Build the Docker image with the provided script
```
Windows Command Prompt: 
  build-docker-image.bat
 
Windows PowerShell:
 .\build-docker-image.bat
 
Linux:
 ./build-docker-image.sh
```

### Quick Validation
We prepared an entry point for the Docker image through which you can validate that the image is working. This validation 
comprises a subset of the experiments which we present in our paper. To be more precise, we configured the validation to
execute 
- three runs of the RQ1 experiment with _RaQuN_ and _Pairwise Descending_ on all experimental subject except _ArgoUML_.  
- three runs of the RQ2 experiment on _PPU Structure_ and _bCMS_.
- one run of the RQ3 experiment with _RaQuN_ and _Pairwise Descending_ up to a _ArgoUML_ subset size of _10%_.

Running the full validation should take around 30 minutes (+- 15 minutes depending on your system).


### Expected Output



## Installation- Running the Experiments without Docker
The following installation instructions apply, if you plan to run the experiments presented in our paper __without__ Docker.

### Installation Instructions


### Basic Usage Example


### Expected Output



## Installation- RaQuN as a Library
The following installation instructions apply, if you plan to use RaQuN as a library in your own project.

### Installation Instructions


### Basic Usage Example


### Expected Output
