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
    
    stage('Building') {
      steps {
        echo 'Building...'
        sh './gradlew clean build'
      }
    }
    
  } 
}
//}
