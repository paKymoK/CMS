# cms-platform

5-region CMS for the CMC Global homepage — one Next.js codebase (`D:\Code\landing-page`),
5 country subdomains, each region's own marketing team maintaining its own content.

Forked from `D:\Code\Workflow`: `auth-service`, `media-service`, `chat-service` (plus their
shared `core-v1`/`infrastructure` modules). `content-service` is net-new.

See `docs/cms-platform-plan.md` for the full plan, decision log, and phase breakdown, and
`CLAUDE.md` for the working rules (multi-tenant discipline, security, what's still missing
from this scaffold).

## Local dev

```
cd backend
./gradlew build          # requires JDK 21 (JAVA_HOME)
cd ../infra
docker compose up
```

Services: `auth-service` :9000, `media-service` :8082, `chat-service` :8083,
`content-service` :8084. No gateway yet — see `CLAUDE.md`'s open items.
