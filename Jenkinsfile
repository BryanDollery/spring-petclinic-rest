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
        stage('Release') {
            steps {
                echo "Released"
            }
        }
    }
}
