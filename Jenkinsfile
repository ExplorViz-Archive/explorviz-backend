//node("linux && jdk8") {
pipeline {
  agent any
  
  stages {  
    stage('Checkout') {
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
    
    /*
    stage('Testing') {
      steps {
        echo 'Building...'
        sh './gradlew clean test'
      }
    }
    
    stage('Building') {
      steps {
        echo 'Building...'
        sh './gradlew clean build'
      }
    }
    */
  } 
}
//}
