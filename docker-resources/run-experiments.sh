#! /bin/bash
if [ "$1" == '' ]
then
  echo "Either fully run a specific experiment one time (run [RQ1|RQ2|RQ3]) or evaluate the results (evaluate) or a do quick setup validation (validate)."
  echo "-- Bash Examples --"
  echo "Run RQ1: './experiments.sh run RQ1'"
  echo "Run RQ2: './experiments.sh run RQ2'"
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
  
  valid_argument=false

  if [ "$1" == 'RQ1' ] || [ "$1" == 'run' ]
  then
      echo "Running experiment for RQ1"
      java -jar RQ1Runner-jar-with-dependencies.jar experiment.properties
      valid_argument=true
  fi
  if [ "$1" == 'RQ2' ] || [ "$1" == 'run' ]
  then
      echo "Running experiment for RQ2"
      java -jar RQ2Runner-jar-with-dependencies.jar experiment.properties
      valid_argument=true
  fi
  if [ "$1" == 'RQ3' ] || [ "$1" == 'run' ]
  then
      echo "Running experiment for RQ3"
      java -jar RQ3Runner-jar-with-dependencies.jar experiment.properties
      valid_argument=true
  fi
  if [ "$1" == 'validate' ]
  then
      echo "Running a 10 minute validation."
      java -jar RQ1Runner-jar-with-dependencies.jar quick-validation.properties
      java -jar RQ2Runner-jar-with-dependencies.jar quick-validation.properties
      java -jar RQ3Runner-jar-with-dependencies.jar quick-validation.properties
      echo "Running result evaluation"
      cd result_analysis_python || exit
      python3.8 evaluation.py
      valid_argument=true
  fi
  if [ $valid_argument == true ]
  then
      echo "Either fully run a specific experiment one time (run [RQ1|RQ2|RQ3]) or evaluate the results (evaluate) or a do quick setup validation (validate)."
      echo "-- Bash Examples --"
      echo "Run RQ1: './experiments.sh run RQ1'"
      echo "Run RQ2: './experiments.sh run RQ2'"
      echo "Evaluate all gathered results: './experiments.sh evaluate'"
      echo "Validate the setup: './experiments.sh validate'"
  fi
fi
