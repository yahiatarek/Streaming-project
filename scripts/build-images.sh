#!/usr/bin/env sh
set -eu

for service in \
  service-registry \
  spring-cloud-config-server \
  limits-service \
  upload-service \
  api-gateway
do
  echo "Building ${service} image..."
  (cd "${service}" && ./mvnw -DskipTests spring-boot:build-image)
done
