#!/bin/bash
echo "Building Docker image"

DIRECTORY="$(basename "$PWD")"
../gradlew assemble

echo $DOCKER_PW | docker login -u $DOCKER_LOGIN --password-stdin

if [[ "$TRAVIS_BRANCH" == 'dev-1' ]]; then
  docker build -t explorviz/explorviz-backend-$DIRECTORY:dev .
  docker push explorviz/explorviz-backend-$DIRECTORY:dev
elif [[ "$TRAVIS_BRANCH" == 'master' ]]; then
  docker build -t explorviz/explorviz-backend-$DIRECTORY:latest .
  docker push explorviz/explorviz-backend-$DIRECTORY:latest
else
  echo "Unknown branch for Docker image."
fi