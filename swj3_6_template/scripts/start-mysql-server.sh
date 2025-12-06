#!/usr/bin/env bash

set -euo pipefail

CONTAINER_NAME="mysql"
NETWORK_NAME="phonebook-net"

if docker ps --format '{{.Names}}' | grep -Fxq "$CONTAINER_NAME"; then
  containerRuns=1
else
  containerRuns=0
fi

if docker ps -a --format '{{.Names}}' | grep -Fxq "$CONTAINER_NAME"; then
  containerExists=1
else
  containerExists=0
fi

if ! docker network ls --format '{{.Name}}' | grep -Fxq "$NETWORK_NAME"; then
  echo "creating network '$NETWORK_NAME' ..."
  docker network create "$NETWORK_NAME" >/dev/null
fi

if [ "$containerRuns" -eq 1 ]; then
  echo "MySQL server is already running."
  exit 0
elif [ "$containerExists" -eq 1 ]; then
  echo "starting existing MySQL container ..."
  docker start "$CONTAINER_NAME" >/dev/null
else
  echo "creating and starting MySQL container ..."
  docker run --name "$CONTAINER_NAME" -d -p 3306:3306 -e "MYSQL_ALLOW_EMPTY_PASSWORD=1" --network "$NETWORK_NAME" mysql:9 >/dev/null 2>&1
fi
