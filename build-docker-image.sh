#! /bin/bash
docker build -t match-experiments \
  --build-arg USER_ID="1000" \
  --build-arg GROUP_ID="1000" .
