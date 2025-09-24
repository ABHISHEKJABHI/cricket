FROM alpine:3.18

# Install OpenJDK 21 on Alpine
RUN apk update && \
    apk add openjdk21-jre

WORKDIR /app
COPY app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
