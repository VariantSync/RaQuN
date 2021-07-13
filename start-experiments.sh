#! /bin/bash

docker run \
--user "$(id -u):$(id -g)" \
--mount source=match-experiments,target=/home/user/results \
match-experiments RQ1

docker run \
--user "$(id -u):$(id -g)" \
--mount source=match-experiments,target=/home/user/results \
match-experiments RQ2

docker run \
--user "$(id -u):$(id -g)" \
--mount source=match-experiments,target=/home/user/results \
match-experiments RQ3

echo "Copying data from the Docker container to ./results"
mkdir -p ./results
docker run --rm --volumes-from match-experiments \
-u "$(id -u):$(id -g)" \
-v "$(pwd)/results/":"/home/user/results" \
ubuntu cp -rf /home/user/results /home/user/results || exit

echo "Removing Docker container and volume"
docker container rm match-experiments
docker volume rm match-experiments

echo "Done."