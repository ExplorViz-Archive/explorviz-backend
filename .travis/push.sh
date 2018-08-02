#!/bin/sh

setup_git() {
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "Travis CI"
}

commit_and_push() {	
  git clone https://$PersonalAccessToken@github.com/ExplorViz/explorviz-docker.git
  cd explorviz-docker
  cp /home/travis/build/ExplorViz/explorviz-backend/build/libs/explorviz-backend-deployment.war explorviz-backend.war
  git add explorviz-backend.war
  git commit -m "Latest backend build artifact on successful Travis build $TRAVIS_BUILD_NUMBER auto-pushed to Docker repository"
  git push https://$PersonalAccessToken@github.com/Explorviz/explorviz-docker.git > /dev/null 2>&1
}

if [ "$TRAVIS_REPO_SLUG" == "ExplorViz/explorviz-backend" ] && [ "$TRAVIS_JDK_VERSION" == "oraclejdk8" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then
  setup_git
  commit_and_push
fi
