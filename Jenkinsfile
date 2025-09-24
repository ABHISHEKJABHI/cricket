pipeline {
      agent {
        docker {
            image 'openjdk:21-jdk'  // This usually works
            args '-v /tmp:/tmp -p 8080:8080'
        }
    }
   
         environment {
        // Centralized environment variables
        DOCKER_REGISTRY = "index.docker.io/v1/"
        DOCKER_USERNAME = "abhishek7483"
        DOCKER_IMAGE_NAME = "cricket"
        GIT_REPO = "https://github.com/ABHISHEKJABHI/cricket.git"
        SONAR_URL = "http://localhost:9000"
    }
      
      stages {
        stage('Setup Maven') {
            steps {
                sh '''
                    apt-get update
                    apt-get install -y maven
                    mvn --version
                '''
            }
        }
        // ... rest of your stages
    }
    
        stage('Checkout') {
            steps {
                git branch: 'main', 
                url: "${GIT_REPO}"
            }
        }
        
        stage('Validate Environment') {
            steps {
                script {
                    // Check required tools
                    sh '''
                        java -version
                        mvn --version
                        docker --version
                    '''
                }
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
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
            environment {
                DOCKER_IMAGE_TAG = "${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:${BUILD_NUMBER}"
            }
            steps {
                script {
                    // Create Dockerfile if it doesn't exist
                    sh '''
                    cat > Dockerfile << EOF
FROM openjdk:21-jre-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \\
    CMD curl -f http://localhost:8080/ || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF
                    '''
                    
                    // Build Docker image
                    docker.build("${DOCKER_IMAGE_TAG}", ".")
                }
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
                            docker login -u $DOCKER_USER -p $DOCKER_PASS
                            docker push ${DOCKER_IMAGE_TAG}
                            
                            # Also tag as latest
                            docker tag ${DOCKER_IMAGE_TAG} ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:latest
                            docker push ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:latest
                        """
                    }
                }
            }
        }
    }
}
