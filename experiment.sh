#! /bin/bash
echo "Starting $1"
mkdir results
docker run --rm --user "$(id -u):$(id -g)" -v "$(pwd)/results":"/home/user/results" match-experiments "$@"

echo "Done."