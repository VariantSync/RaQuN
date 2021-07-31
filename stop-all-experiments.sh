#! /bin/bash
echo "Stopping all running experiments. This will take a moment..."
docker stop $(docker ps -a -q --filter "ancestor=match-experiments")
echo "...done."