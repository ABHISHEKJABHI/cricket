pipeline {
    agent {
        docker {
            image 'maven:3.9.6-eclipse-temurin-21'
            args '-v /tmp:/tmp -p 8080:8080'
        }
    }
    
    environment {
        DOCKER_USERNAME = "abhishek7483"
        DOCKER_IMAGE_NAME = "cricket"
        GIT_REPO = "https://github.com/ABHISHEKJABHI/cricket.git"
        SONAR_URL = "http://localhost:9000"
    }
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: "${GIT_REPO}"
            }
        }
        
        stage('Validate Environment') {
            steps {
                sh '''
                    java -version
                    mvn --version
                    echo "Docker operations will run on Jenkins host"
                '''
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                
                // Save the built JAR for Docker stage
                sh 'cp target/*.jar ./app.jar'
                stash name: 'app-jar', includes: 'app.jar'
            }
        }
        
        stage('Static Code Analysis') {
            environment {
                SONAR_SCANNER_OPTS = "-Xmx1024m"
            }
            steps {
                withCredentials([string(credentialsId: 'sonarqube', variable: 'SONAR_AUTH_TOKEN')]) {
                    sh """
                        mvn sonar:sonar \
                        -Dsonar.login=$SONAR_AUTH_TOKEN \
                        -Dsonar.host.url=${SONAR_URL} \
                        -Dsonar.projectKey=cricket-game \
                        -Dsonar.projectName='Cricket Game' \
                        -Dsonar.java.binaries=target/classes
                    """
                }
            }
        }
        
        stage('Build Docker Image') {
            agent any  // ✅ Run on Jenkins host (has Docker)
            environment {
                DOCKER_IMAGE_TAG = "${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:${BUILD_NUMBER}"
            }
            steps {
                // Unstash the JAR from previous stage
                unstash 'app-jar'
                
                script {
                    // Create Dockerfile
                    sh '''
                    cat > Dockerfile << EOF
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF
                    '''
                    
                    // Build Docker image
                    docker.build("${DOCKER_IMAGE_TAG}", ".")
                }
            }
        }
        
        stage('Push Docker Image') {
            agent any  // ✅ Run on Jenkins host (has Docker)
            steps {
                script {
                    withCredentials([usernamePassword(
                        credentialsId: 'dockerhub',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh """
                            docker login -u $DOCKER_USER -p $DOCKER_PASS
                            docker push ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:${BUILD_NUMBER}
                            docker tag ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:${BUILD_NUMBER} ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:latest
                            docker push ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:latest
                        """
                    }
                }
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
    }
}
