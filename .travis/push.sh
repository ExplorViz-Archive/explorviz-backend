#!/bin/sh

setup_git() {
  touch ~/.ssh/id_rsa
  echo ${deploy-key} > ~/.ssh/id_rsa
  git config git config core.sshCommand "ssh -i ~/.ssh/id_rsa -F /dev/null" 
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
  git remote add https://git@github.com/explorviz/explorviz-docker.git > /dev/null 2>&1
  git push --quiet > /dev/null 2>&1
}

setup_git
commit_build_files
upload_files