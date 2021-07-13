#! /bin/bash
docker build -t match-experiments \
  --build-arg USER_ID="$(id -u)" \
  --build-arg GROUP_ID="$(id -g)" .
