# cms-platform

## Local dev

```
cd backend
./gradlew build          # requires JDK 21 (JAVA_HOME)
cd ../infra
docker compose up
```

Services: `auth-service` :9000, `media-service` :8082, `chat-service` :8083,
`content-service` :8084. No gateway yet — see `CLAUDE.md`'s open items.
