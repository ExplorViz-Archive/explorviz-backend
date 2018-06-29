//node("linux && jdk8") {
pipeline {
  agent any
  
  stage('Checkout'){
    git branch: 'jenkins', url: 'https://github.com/ExplorViz/explorviz-backend/'
  }

  stage('Build'){
    sh './gradlew clean build'
  }
  
}
//}
