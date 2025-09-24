FROM alpine:3.18

# Install OpenJDK 17 (available in Alpine repositories)
RUN apk update && \
    apk add openjdk17-jre

WORKDIR /app
COPY app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
