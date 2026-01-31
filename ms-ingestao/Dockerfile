# ---- Stage 1: Build ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom first to leverage Docker layer caching
COPY pom.xml ./
# Pre-download dependencies (will fail harmlessly if modules missing; then we'll copy sources)
RUN mvn -B -q -e -DskipTests dependency:go-offline || true

# Copy source code
COPY src ./src

# Build the application
RUN mvn -B -DskipTests package

# ---- Stage 2: Runtime (JRE only) ----
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /opt/app

# Install curl for docker healthcheck
RUN apk add --no-cache curl

# Copy fat jar from build stage
COPY --from=build /app/target/ms-ingestao-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# JVM options can be overridden at runtime via JAVA_OPTS
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
