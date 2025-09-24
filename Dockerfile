# Multi-stage build for optimized image size
FROM maven:3.8.6-openjdk-11 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:11-jre-slim

# Install curl for health checks and basic utilities
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean

# Create non-root user for security
RUN groupadd -r cricket && useradd -r -g cricket cricket

# Create application directory
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder --chown=cricket:cricket /app/target/cricket-game-1.0.0.jar app.jar

# Copy any additional resources if needed
# COPY --from=builder --chown=cricket:cricket /app/target/classes ./resources

# Switch to non-root user
USER cricket

# Expose application port
EXPOSE 8080

# Add health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/ || exit 1

# JVM optimization flags
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Djava.security.egd=file:/dev/./urandom"

# Application entry point
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
