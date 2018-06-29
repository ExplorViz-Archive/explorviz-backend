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
    
    stage('Quality Checks') {
      steps {
        echo 'Performing software quality checks...'
        echo 'Running checkstyle...'
        sh './gradlew checkstyle'
        echo 'Running pmd...'
        sh './gradlew pmd'
        echo 'Running findbugs...'
        sh './gradlew findbugs'
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
