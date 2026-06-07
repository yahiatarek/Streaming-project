#!/usr/bin/env sh
set -eu

ENV_FILE="${ENV_FILE:-.env.prod}"

wait_for_url() {
  name="$1"
  url="$2"
  attempts="${3:-60}"

  i=1
  while [ "$i" -le "$attempts" ]; do
    if curl -fsS "$url" >/dev/null 2>&1; then
      echo "${name} is ready"
      return 0
    fi

    echo "Waiting for ${name} (${i}/${attempts})..."
    i=$((i + 1))
    sleep 2
  done

  echo "Timed out waiting for ${name}" >&2
  return 1
}

docker compose --env-file "$ENV_FILE" up -d service-registry
wait_for_url "service-registry" "http://localhost:8761"

docker compose --env-file "$ENV_FILE" up -d config-server
wait_for_url "config-server" "http://localhost:8888/upload-service/dev"

docker compose --env-file "$ENV_FILE" up
