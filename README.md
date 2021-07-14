# TODO
- Instruction where and how to obtain artifacts
- Clear description of how to repeat/replicate/reproduce
- Artifacts which focus on data should
    - cover aspects relevant to understand the context
    - understand data provenance
    - ethical and legal statements (if relevant)
    - storage requirements
- Artifacts which focus on software should
    - cover aspects relevant to how to install und use it

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

#### result_analysis_python
This is the python project containing the python scripts that were used to evaluate the experimental results. We recommend
to use [Pycharm](https://www.jetbrains.com/pycharm/) as IDE. You should open this directory as a project.
