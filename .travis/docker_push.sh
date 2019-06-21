#!/bin/bash
echo "Building Docker image"

DIRECTORY="$(basename "$PWD")"
../gradlew assemble

echo $DOCKER_PW | docker login -u $DOCKER_LOGIN --password-stdin

if [[ "$TRAVIS_BRANCH" == 'dev-1' ]]; then
  docker build -t explorviz-backend-$DIRECTORY .

  docker tag explorviz-backend-$DIRECTORY explorviz/explorviz-backend-$DIRECTORY:dev-$TRAVIS_COMMIT

  docker tag explorviz-backend-$DIRECTORY explorviz/explorviz-backend-$DIRECTORY:dev

  docker push explorviz/explorviz-backend-$DIRECTORY
elif [[ "$TRAVIS_BRANCH" == 'master' ]]; then
  docker build -t explorviz-backend-$DIRECTORY .

  docker tag explorviz-backend-$DIRECTORY explorviz/explorviz-backend-$DIRECTORY:latest-$TRAVIS_COMMIT

  docker tag explorviz-backend-$DIRECTORY explorviz/explorviz-backend-$DIRECTORY:latest

  docker push explorviz/explorviz-backend-$DIRECTORY
else
  echo "Unknown branch for Docker image."
fi