pipeline {
  agent any
  triggers {
    pollSCM('* * * * *')
  }
  options {
    buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '10'))
    timestamps()
  }
  stages {
    stage("verify tooling") {
      steps {
        sh '''
          docker version
          docker info
          docker compose version
          curl --version
        '''
      }
    }
    stage('Prune Docker data') {
        steps {
            sh 'docker system prune -a --volumes -f'
        }
    }
    stage('Start container') {
        steps {
            sh 'docker compose up -d --no-color --wait --build'
        }
    }
  }
  post {
      always {
        sh 'docker compose ps'
      }
    }
}