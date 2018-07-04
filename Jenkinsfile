#!/usr/bin/env groovy

//node("linux && jdk8") {
pipeline {
  agent any
  
  options {
    skipDefaultCheckout(true)
  }
  
  stages {  
    stage('Cloning') {
      steps {
        echo 'Cloning...'
        git branch: 'jenkins', url: 'https://github.com/ExplorViz/explorviz-backend/'
      }   
    }

    stage('Testing') {
      steps {
        echo 'Building...'
        sh './gradlew clean test'
      }
    }
    
    stage('Checking') {
      steps {
        echo 'Performing quality checks...'
        echo 'Running checkstyle, pmd, and findbugs...'
        sh './gradlew check'
      }
    }
          
    stage('Building') {
      steps {
        echo 'Building...'
        sh './gradlew build'
      }
    }
    
  } 
}
//}
