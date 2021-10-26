#!/bin/bash

start() {
  docker run -d \
      --rm \
      --health-cmd "pg_isready -d elefante -U elefante" \
      --health-interval "1s" \
      --health-retries 	"3" \
      --name postgres \
      --network host \
      -e POSTGRES_PASSWORD=elefantinho \
      -e POSTGRES_DATABASE=elefante \
      -e POSTGRES_USER=elefante \
      postgres:12-alpine
}

stop() {
  docker kill $(docker ps --filter "name=postgres" --quiet)
}

echo $1

case $1 in
  "start") start;;
  "stop") stop;;
  *)
    echo "WRONG OPTION. Use: 'start' or 'stop'"
    exit 1
  ;;
esac