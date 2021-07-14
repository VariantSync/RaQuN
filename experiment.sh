#! /bin/bash
echo "Starting $1"
docker run --rm --user "1000:1000" -v "$(pwd)/results":"/home/user/results" match-experiments "$@"

echo "Done."