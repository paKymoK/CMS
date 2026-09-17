# Local PostgreSQL — Dev Setup

Custom PostgreSQL image with all service databases pre-created, running in Docker for local development.

## Start

```bash
docker-compose up -d
```

## Stop

```bash
docker-compose down
```

> Omit `-v` to keep the data volume between restarts. Add `-v` only if you want a clean slate.

---

## Connection

| Field | Value |
|-------|-------|
| Host | `localhost` |
| Port | `5433` |
| Username | `postgres` |
| Password | `postgres` |

---

## Databases

The following databases are created automatically on first start via `init-databases.sql`:

| Database | Used by |
|----------|---------|
| `authentication` | auth-service |
| `workflow` | workflow-service |
| `media` | media-service |
| `shop` | shop-service |

---

## Notes

- Uses a custom image (`tqthai/postgres`) built from the local `Dockerfile` with a tuned `postgresql.conf`
- The `init-databases.sql` script only runs once on a fresh volume — re-running `docker-compose up` on an existing volume will not re-run it
- To apply schema changes, connect directly with a client (e.g. psql, DBeaver) or use the service's own Liquibase migration

## Re-initializing

If you need to recreate the databases from scratch:

```bash
docker-compose down -v && docker-compose up -d
```
