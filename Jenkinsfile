pipeline {
  agent {
    docker {
      image 'maven:3.6-jdk-8'
      args '-v /home/jenkins/.m2:/var/maven/.m2 -e MAVEN_CONFIG=/var/maven/.m2 -e MAVEN_OPTS=-Duser.home=/var/maven'
    }
  }
  environment {
    GPG_SECRET = credentials('gpg_password')
  }
  stages {
    stage('Build') {
      steps {
        sh 'mvn clean deploy -Prelease -Dgpg.passphrase=${GPG_SECRET}'
      }
    }
    stage('javadoc') {
      steps {
        sh 'mvn javadoc:javadoc'
      }
    }
  }
}
