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

## Database migrations

Each service that owns a database (auth, media, chat, content) keeps its Liquibase changelog as two
files under `src/main/resources/db/changelog/init/`:

- `init-schema.sql` — the whole schema, in its current form.
- `init-data.sql` — the initial data (chat-service has none).

There is no version history on purpose: while the platform is developed by one person against
disposable data, a schema change means editing `init-schema.sql` and recreating the local database
(`DROP DATABASE` / `CREATE DATABASE`, then start the service). Liquibase checksums every applied
file, so an existing database must be recreated after any edit. Once a database holds data that
matters, switch to additive changesets again.

`init-data.sql` for auth is a **dev bootstrap**: it creates the `admin` account (password `admin`,
stored bcrypt-encoded). Change or remove it before any environment other than local development.

## Seed images

The seeded `en` content (posts, case studies, offices, service cards, logos, testimonial photos)
references images in **media-service**, not the website's static folder:

- `media-service` bundles the image files in `src/main/resources/seed-images/<id><ext>`,
  registers them in the `en` media library in its `init-data.sql`, and copies the files into
  `storage.images-dir` at startup (`SeedImageInstaller`; set `media.seed-images.enabled=false` in
  an environment that shouldn't carry seed data).
- `content-service`'s `init-data.sql` points the seeded rows at those ids.
- Media references are stored as **origin-less paths** — `/media-service/images/<id><ext>` — so the
  data carries no host. admin-app (`lib/media.ts`) and the website (`lib/cms/media.ts`) resolve
  them against their own gateway base URL. Absolute URLs still work.

A fresh environment needs nothing beyond starting `media-service` and `content-service`.
