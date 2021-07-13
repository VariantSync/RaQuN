#! /bin/bash

    docker run \
    --user "$(id -u):$(id -g)" \
    --mount source=match-experiments-"$1",target=/home/user/results \
    match-experiments

    echo "Copying data from the Docker container to ./results"
    mkdir -p ./results
    docker run --rm --volumes-from match-experiments-"$1" \
    -u "$(id -u):$(id -g)" \
    -v "$(pwd)/results/":"/home/user/results" \
    ubuntu cp -rf /home/user/results /home/user/results || exit

    echo "Removing Docker container and volume"
    docker container rm variability-extraction-busybox
    docker volume rm busybox-extraction

echo "Done."