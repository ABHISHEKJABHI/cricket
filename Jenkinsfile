pipeline {
    agent any   // ✅ Runs entirely on Jenkins host

    environment {
        DOCKER_USERNAME   = "abhishek7483"
        DOCKER_IMAGE_NAME = "cricket"
        GIT_REPO          = "https://github.com/ABHISHEKJABHI/cricket.git"
        SONAR_URL         = "http://localhost:9000"
    }
      tools {
        maven 'mvn'
      
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: "${GIT_REPO}"
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                sh 'cp target/*.jar ./app.jar'
                stash name: 'app-jar', includes: 'app.jar'
            }
        }

        stage('Static Code Analysis') {
            steps {
                withCredentials([string(credentialsId: 'sonarqube', variable: 'SONAR_AUTH_TOKEN')]) {
                    sh """
                        mvn sonar:sonar \
                        -Dsonar.login=$SONAR_AUTH_TOKEN \
                        -Dsonar.host.url=${SONAR_URL} \
                         -Dsonar.projectKey=cricket-scan \
                         -Dsonar.projectName='cricket-scan' \
                        -Dsonar.java.binaries=target/classes
                    """
                }
            }
        }

       stage('Build Docker Image') {
    environment {
        DOCKER_IMAGE_TAG = "${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:${BUILD_NUMBER}"
    }
    steps {
        unstash 'app-jar'
        sh """
            git clone https://github.com/ABHISHEKJABHI/cricket.git .
            docker build -t ${DOCKER_IMAGE_TAG} .
        """
    }
}

stage('Push Docker Image') {
    steps {
        script {
            withCredentials([usernamePassword(
                credentialsId: 'dockerhub',
                usernameVariable: 'DOCKER_USER',
                passwordVariable: 'DOCKER_PASS'
            )]) {
                sh """
                    echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                    docker push ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:${BUILD_NUMBER}
                    docker tag ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:${BUILD_NUMBER} ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:latest
                    docker push ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:latest
                """
            }
        }
    }
  }
}
}
