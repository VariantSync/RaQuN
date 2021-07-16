#! /bin/bash
echo "Stopping all running experiments."
docker stop $(docker ps -a -q --filter "ancestor=match-experiments")