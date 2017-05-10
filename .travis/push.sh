#!/bin/sh

setup_git() {
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "Travis CI"
}

commit_build_files() {
  git checkout
  cp target/explorviz-ui-backend-*.war explorviz-backend.war  
  git add explorviz-backend.war
  git commit --message "Travis build: $TRAVIS_BUILD_NUMBER"
}

upload_files() {
  git push --quiet https://$PersonalAccessToken@github.com/explorviz/explorviz-docker.git > /dev/null 2>&1
}

setup_git
commit_build_files
upload_files