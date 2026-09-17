FROM gradle:8.14.3-jdk21-alpine AS builder
WORKDIR /app

COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY infrastructure infrastructure
COPY core-v1 core-v1
COPY content-service content-service

RUN gradle :content-service:bootJar -x test --no-daemon

FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=builder /app/content-service/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
