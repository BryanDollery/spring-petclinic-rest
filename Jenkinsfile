pipeline {
    agent {
        docker {
            image 'bryandollery/maven-repo' 
        }
    }
    stages {
        stage('Build') { 
            steps {
                sh 'mvn clean compile' 
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Deliver') {
            steps {
                sh './jenkins/scripts/deliver.sh'
            }
        }
    }
}
