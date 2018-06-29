node("linux && jdk8") {

  stage('Checkout'){
    git branch: 'jenkins', url: 'https://github.com/ExplorViz/explorviz-backend/'
  }

  stage('Build'){
    sh './gradlew clean build'
  }
  
}
