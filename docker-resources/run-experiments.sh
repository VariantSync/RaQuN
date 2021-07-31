#! /bin/bash
if [ "$1" == '' ]
then
  echo "Either fully run a specific experiment one time (RQ1|RQ2|RQ3), run all experiments (run), evaluate the results (evaluate), or a do quick setup validation (validate)."
  echo "-- Bash Examples --"
  echo "Run RQ1: './experiments.sh RQ1'"
  echo "Run RQ2: './experiments.sh RQ2'"
  echo "Evaluate all gathered results: './experiments.sh evaluate'"
  echo "Validate the setup: './experiments.sh validate'"
  exit
fi

echo "Starting $1"

cd /home/user || exit
ls -l

if [ "$1" == 'evaluate' ]
then
    echo "Running result evaluation"
    cd result_analysis_python || exit
    python3.8 evaluation.py
    exit
else
  echo "Copying jars"
  cp target/*Runner*-jar-with* .
  echo ""

  echo "Files in WORKDIR"
  ls -l
  echo ""

  if [ "$1" == 'run' ]
  then
      echo "Running all experiments."
      echo "Running experiment for RQ1"
      java -Xmx16G -jar RQ1Runner-jar-with-dependencies.jar full-experiments.properties
      echo "Running experiment for RQ2"
      java -Xmx16G -jar RQ2Runner-jar-with-dependencies.jar full-experiments.properties
      echo "Running experiment for RQ3"
      java -Xmx16G -jar RQ3Runner-jar-with-dependencies.jar full-experiments.properties $2
  elif [ "$1" == 'RQ1' ]
  then
      echo "Running experiment for RQ1"
      java -Xmx16G -jar RQ1Runner-jar-with-dependencies.jar single-experiment.properties
  elif [ "$1" == 'RQ2' ]
  then
      echo "Running experiment for RQ2"
      java -Xmx16G -jar RQ2Runner-jar-with-dependencies.jar single-experiment.properties
  elif [ "$1" == 'RQ3' ]
  then
      echo "Running experiment for RQ3"
      java -Xmx16G -jar RQ3Runner-jar-with-dependencies.jar single-experiment.properties $2
  elif [ "$1" == 'validate' ]
  then
      echo "Running a (hopefully) short validation. This might take up to one hour."
      java -Xmx16G -jar RQ1Runner-jar-with-dependencies.jar quick-validation.properties
      java -Xmx16G -jar RQ2Runner-jar-with-dependencies.jar quick-validation.properties
      java -Xmx16G -jar RQ3Runner-jar-with-dependencies.jar quick-validation.properties 1
      java -Xmx16G -jar RQ3Runner-jar-with-dependencies.jar quick-validation.properties 2
      java -Xmx16G -jar RQ3Runner-jar-with-dependencies.jar quick-validation.properties 3
      echo "Running result evaluation"
      cd result_analysis_python || exit
      python3.8 evaluation.py
  else
      echo "Either fully run a specific experiment one time (RQ1|RQ2|RQ3), run all experiments (run), evaluate the results (evaluate), or a do quick setup validation (validate)."
      echo "-- Bash Examples --"
      echo "Run RQ1: './experiments.sh RQ1'"
      echo "Run RQ2: './experiments.sh RQ2'"
      echo "Evaluate all gathered results: './experiments.sh evaluate'"
      echo "Validate the setup: './experiments.sh validate'"
  fi
fi