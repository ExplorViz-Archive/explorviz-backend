#!/bin/sh

setup_git() {
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "Travis CI"
}

commit_build_files() {
  git checkout
  git add . explorviz-ui-backend-*.war
  git commit --message "Travis build: $TRAVIS_BUILD_NUMBER"
}

upload_files() {
  git remote add https://${GH_TOKEN}@github.com/explorviz/explorviz-docker.git > /dev/null 2>&1
  git push --quiet > /dev/null 2>&1
}

setup_git
commit_build_files
upload_files