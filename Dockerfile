# syntax=docker/dockerfile:1.4
FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /workspace

# Pre-fetch dependencies into a cached layer
COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -B

# Copy all sources
COPY src ./src

# Build in parallel, skip tests
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests -B -T1C

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=builder /workspace/target/betting-settlement-service-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
