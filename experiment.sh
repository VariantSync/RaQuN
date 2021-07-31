#! /bin/bash
echo "Starting $1"
mkdir results
docker run --rm -v "$(pwd)/results":"/home/user/results" match-experiments "$@"

echo "Done."