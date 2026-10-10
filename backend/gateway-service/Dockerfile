# syntax=docker/dockerfile:1
FROM gradle:8.14.3-jdk21-alpine AS builder
WORKDIR /app

COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY infrastructure infrastructure
COPY core-v1 core-v1
COPY gateway-service gateway-service

RUN --mount=type=cache,target=/home/gradle/.gradle,id=gradle-cache-shared,sharing=locked \
    gradle :gateway-service:bootJar -x test --no-daemon --build-cache

FROM amazoncorretto:21-alpine
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app
COPY --from=builder /app/gateway-service/build/libs/*.jar app.jar
RUN mkdir -p /logs && chown spring:spring /logs
USER spring
HEALTHCHECK --interval=15s --timeout=3s --start-period=30s --retries=5 \
    CMD wget -qO- http://localhost:8090/actuator/health | grep -q '"status":"UP"' || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
