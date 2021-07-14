#! /bin/bash

docker run --rm --user "1000:1000" -v "$(pwd)/results":"/home/user/results" match-experiments "$@"

echo "Done."