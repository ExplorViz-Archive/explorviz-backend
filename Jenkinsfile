#!/usr/bin/env groovy

pipeline {
  agent any
  
  options {
    skipDefaultCheckout(true)
  }
  
  stages {  
    stage('Cloning') {
      steps {
        echo 'Cloning...'
        git branch: 'master', url: 'https://github.com/ExplorViz/explorviz-backend/'
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
    
    stage('Sonarqube') {
      steps {
        echo 'Executing Sonarqube...'
        sh './gradlew sonarqube \
              -Dsonar.organization=explorviz \
              -Dsonar.host.url=https://sonarcloud.io \
              -Dsonar.login=687f4a75d3eedf589f513894b46561ffa00e01a2'
      }
    }
    
  } 
}
