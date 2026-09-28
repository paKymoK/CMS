# syntax=docker/dockerfile:1
FROM gradle:8.14.3-jdk21-alpine AS builder
WORKDIR /app

COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY infrastructure infrastructure
COPY core-v1 core-v1
COPY media-service media-service

RUN --mount=type=cache,target=/home/gradle/.gradle,id=gradle-cache-shared,sharing=locked \
    gradle :media-service:bootJar -x test --no-daemon --build-cache

FROM amazoncorretto:21-alpine
RUN apk add --no-cache ffmpeg \
    && addgroup -S spring && adduser -S spring -G spring

WORKDIR /app
COPY --from=builder /app/media-service/build/libs/*.jar app.jar

ENV FFMPEG_PATH=/usr/bin/ffmpeg
ENV FFPROBE_PATH=/usr/bin/ffprobe

USER spring
HEALTHCHECK --interval=15s --timeout=3s --start-period=30s --retries=5 \
    CMD wget -qO- http://localhost:8082/actuator/health | grep -q '"status":"UP"' || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
