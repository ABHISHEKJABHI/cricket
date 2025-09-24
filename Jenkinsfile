pipeline {
    agent {
        docker {
            image 'jelastic/maven:3.9.5-openjdk-21'  // Maven + OpenJDK 21
            args '-v $HOME/.m2:/root/.m2'   // Optional: cache Maven dependencies
        }
    }

    environment {
        DOCKER_USERNAME   = "abhishek7483"
        DOCKER_IMAGE_NAME = "cricket"
        GIT_REPO          = "https://github.com/ABHISHEKJABHI/cricket.git"
        SONAR_URL         = "http://localhost:9000"
        // Set DOCKER_IMAGE_TAG globally to avoid MissingPropertyException
        DOCKER_IMAGE_TAG  = "${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:${BUILD_NUMBER}"
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
                stash name: 'app-jar', includes: 'target/*.jar'
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
            steps {
                unstash 'app-jar'
                sh """
                    mv target/*.jar app.jar
                    docker build -t ${DOCKER_IMAGE_TAG} .
                """
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'abhishek7483',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh """
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push ${DOCKER_IMAGE_TAG}
                        docker tag ${DOCKER_IMAGE_TAG} ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:latest
                        docker push ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:latest
                    """
                }
            }
        }
    }
}
