#!/bin/bash


BRANCH_NAME=$(git branch --show-current)

for DIRECTORY in *-service; do
    if [ -d "${DIRECTORY}" ]; then
        cd "${DIRECTORY}"
        ../gradlew assemble
        docker build -t explorviz/explorviz-backend-$DIRECTORY:$BRANCH_NAME .

        ## Add your local images to kind
        kind load --name istio-testing docker-image explorviz/explorviz-backend-$DIRECTORY:$BRANCH_NAME
        cd -
    fi
done
