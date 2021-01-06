pipeline {
  agent {
    docker {
      image 'maven:3.5.4-jdk-8-alpine'
      args '-v /root/.m2:/root/.m2'
    }
  }
  stages {
    stage('Build') {
      steps {
        sh 'mvn clean package'
      }
    }
    stage('Post') {
      steps {
        archiveArtifacts(excludes: 'target/original-*.*', onlyIfSuccessful: true, artifacts: 'target/*.jar')
      }
    }
  }
}