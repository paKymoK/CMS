FROM gradle:8.14.3-jdk21-alpine AS builder
WORKDIR /app

COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY infrastructure infrastructure
COPY core-v1 core-v1
COPY chat-service chat-service

RUN gradle :chat-service:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /app/chat-service/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
