#!/bin/bash


BRANCH_NAME=$(git branch --show-current)

for DIRECTORY in *-service; do
    if [ -d "${DIRECTORY}" ]; then
        cd "${DIRECTORY}"
        ## uncomment if they should also be built
        # ../gradlew assemble
        # docker build -t explorviz/explorviz-backend-$D:local .

        ## Add your local images to kind
        kind load --name istio-testing docker-image explorviz/explorviz-backend-$D:$BRANCH_NAME
        cd -
    fi
done