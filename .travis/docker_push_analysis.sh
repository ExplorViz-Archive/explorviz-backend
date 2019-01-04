#!/bin/bash
echo "Building Docker image"
cd analysis
../gradlew assemble
echo $DOCKER_PW | docker login -u $DOCKER_LOGIN --password-stdin

if [[ $TRAVIS_BRANCH == 'dev-1' ]]
  docker build -t explorviz/explorviz-backend-analysis:dev .
  docker push explorviz/explorviz-backend-analysis:dev
else
  docker build -t explorviz/explorviz-backend-analysis:latest .
  docker push explorviz/explorviz-backend-analysis:latest
fi