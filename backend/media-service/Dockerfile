FROM gradle:8.14.3-jdk21-alpine AS builder
WORKDIR /app

COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY infrastructure infrastructure
COPY core-v1 core-v1
COPY media-service media-service

RUN gradle :media-service:bootJar -x test --no-daemon

FROM amazoncorretto:21-alpine
RUN apk add --no-cache ffmpeg

WORKDIR /app
COPY --from=builder /app/media-service/build/libs/*.jar app.jar

ENV FFMPEG_PATH=/usr/bin/ffmpeg
ENV FFPROBE_PATH=/usr/bin/ffprobe

ENTRYPOINT ["java", "-jar", "app.jar"]
