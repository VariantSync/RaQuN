#! /bin/bash
echo "Starting extraction"
java -version

cd /home/user || exit
ls -l

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
else
    echo "Select an experiment to run [RQ1|RQ2|RQ3]"
fi