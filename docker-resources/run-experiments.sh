#! /bin/bash
if [ "$1" == '' ]
then
  echo "Select an experiment to run [RQ1|RQ2|RQ3] or run the evaluation (EVAL) or a quick validation (VALIDATE)."
  echo "Example: './experiments.sh RQ1'"
  exit
fi

echo "Starting $1"

cd /home/user || exit
ls -l

if [ "$1" == 'EVAL' ]
then
    echo "Running result evaluation"
    cd result_analysis_python || exit
    python3.8 evaluation.py
    exit
else
  echo "Building with Maven"
  mvn package || exit
  echo ""

  echo "Copying jars"
  cp target/*Runner*-jar-with* .
  echo ""

  echo "Files in WORKDIR"
  ls -l
  echo ""

  if [ "$1" == 'RQ1' ]
  then
      echo "Running experiment for RQ1"
      java -jar RQ1Runner-jar-with-dependencies.jar experiment.properties
  elif [ "$1" == 'RQ2' ]
  then
      echo "Running experiment for RQ2"

      java -jar RQ2Runner-jar-with-dependencies.jar experiment.properties
  elif [ "$1" == 'RQ3' ]
  then
      echo "Running experiment for RQ3"
      java -jar RQ3Runner-jar-with-dependencies.jar experiment.properties
  elif [ "$1" == 'VALIDATE' ]
  then
      echo "Running a 10 minute validation."
      java -jar RQ1Runner-jar-with-dependencies.jar quick-validation.properties
      java -jar RQ2Runner-jar-with-dependencies.jar quick-validation.properties
  else
      echo "Select an experiment to run [RQ1|RQ2|RQ3] or run the evaluation (EVAL) or a quick validation (VALIDATE)."
      echo "Example: './experiments.sh RQ1'"
  fi
fi
