# API Contract

Not yet generated. `content-service` (and the forked `auth-service`/`media-service`/`chat-service`)
expose Swagger UI once running:

- `content-service`: `http://localhost:8084/v1/doc/swagger-ui.html`
- `media-service`: see its own `springdoc.yml` for the exact path
- `auth-service` / `chat-service`: same pattern

Keep this file current as endpoints change (Phase 6 formalizes this via OpenAPI export)
— the admin app and the separately-built Next.js sites both depend on it.
