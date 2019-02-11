#!/bin/bash

echo -e "Publishing javadoc...\n"

./gradlew alljavadoc 
cp -R docs $HOME/docs

cd $HOME
git config --global user.email "travis@travis-ci.org"
git config --global user.name "Travis CI"
git clone --quiet --branch=gh-pages https://$PersonalAccessToken@github.com/ExplorViz/explorviz-backend gh-pages > /dev/null

cd gh-pages
git rm -rf *
cp -Rf $HOME/docs/* .
git add -f .
git commit -m "Latest Javadoc on successful Travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
git push -fq origin gh-pages > /dev/null

echo -e "Published Javadoc to gh-pages.\n"
  
