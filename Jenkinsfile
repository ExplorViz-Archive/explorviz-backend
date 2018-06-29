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

    stage('Build') {
      steps {
        echo 'Building...'
        sh './gradlew clean build'
      }
    }
    
  } 
}
//}
