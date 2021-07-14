#! /bin/bash
echo "Stopping all running experiments (and other docker containers)"
docker stop $(docker ps -a -q --filter ancestor=match-experiments)