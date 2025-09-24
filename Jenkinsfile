pipeline {
    agent {
        docker {
            image 'maven:3.9.6-eclipse-temurin-21'
            args '-v /tmp:/tmp'
        }
    }

    environment {
        DOCKER_USERNAME   = "abhishek7483"
        DOCKER_IMAGE_NAME = "cricket"
        GIT_REPO          = "https://github.com/ABHISHEKJABHI/cricket.git"
        SONAR_URL         = "http://localhost:9000"
        MAVEN_REPO_LOCAL  = "/tmp/.m2/repository"   // ✅ Writable inside container
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
                    echo "✅ Docker operations will run on Jenkins host"
                '''
            }
        }

        stage('Build') {
            steps {
                sh "mvn clean package -DskipTests -Dmaven.repo.local=${MAVEN_REPO_LOCAL}"

                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true

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
                        -Dmaven.repo.local=${MAVEN_REPO_LOCAL} \
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
            agent any
            environment {
                DOCKER_IMAGE_TAG = "${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:${BUILD_NUMBER}"
            }
            steps {
                unstash 'app-jar'

                script {
                    writeFile file: 'Dockerfile', text: '''
                    FROM eclipse-temurin:21-jre
                    WORKDIR /app
                    COPY app.jar app.jar
                    EXPOSE 8080
                    ENTRYPOINT ["java", "-jar", "app.jar"]
                    '''

                    docker.build("${DOCKER_IMAGE_TAG}", ".")
                }
            }
        }

        stage('Push Docker Image') {
            agent any
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

    post {
        always {
            cleanWs()
        }
    }
}
