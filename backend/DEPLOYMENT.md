# Deployment (one VPS per service, host networking)

Every compose stack uses `network_mode: host` (Linux only). A container listens on the
VPS's own ports and `127.0.0.1` means that VPS. With no `.env`, every default points at
`127.0.0.1`, so the whole stack also runs on a single Linux host.

## Per-VPS `.env`

Copy each service's `.env.example` to `.env` and set only what differs:

| Variable | Production value |
|---|---|
| `EUREKA_INSTANCE_IP_ADDRESS` (auth, chat, content, media) | **this** VPS's private IP; the gateway reaches the service on it |
| `EUREKA_URI` | `http://<discovery private IP>:8761/eureka` |
| `DB_HOST` | `127.0.0.1` if Postgres runs on this VPS, otherwise its private IP |
| `SPRING_DATA_REDIS_HOST` | Redis VPS private IP |
| `JWK_URI` | `http://<auth private IP>:9000/oauth2/jwks` |
| `AUTH_ISSUER_URI` | auth-service **public** URL (`https://auth.example.com`), the same value in every service. It is the token `iss` and the base of the OIDC endpoints the browser is sent to, so every service must be able to reach this URL |
| `MANAGEMENT_OTLP_*` | `http://127.0.0.1:4318/...` (Alloy on the same VPS) |
| `DB_PASSWORD`, `LDAP_*` | real secrets; never keep the defaults |

## Ports and firewall

Host mode publishes nothing through Docker, so the firewall (`ufw`/cloud security group)
is the only access control. Default deny; allow:

| Port | Service | Allow from |
|---|---|---|
| 8080 | gateway (proxy) | public (behind TLS reverse proxy) |
| 8090 | gateway actuator | monitor VPS only |
| 9000 | auth-service | public (login/OIDC, via TLS) + all service VPSs |
| 8761 | discovery (Eureka, no auth) | service VPSs only |
| 8082 / 8083 / 8086 | media / chat / content | gateway VPS only (+ monitor for health) |
| 6379 | Redis | auth, chat, media, gateway VPSs |
| 5432 | Postgres | the VPSs that use it |
| 4317 / 4318 / 12345 | Alloy (OTLP, UI) | localhost only; block externally |
| 9090 / 3101 / 4319 | Prometheus / Loki / Tempo (monitor VPS) | every VPS running Alloy |
| 3100 | Grafana | admins |

## Startup order

1. Postgres and Redis. Then monitor (optional) and Alloy.
2. discovery-service.
3. auth-service, media-service, chat-service, content-service.
4. gateway-service.

`depends_on` is not used, because the dependencies live on other machines; services retry
Eureka registration on their own.

## Notes

- The monitor stack stays on a Docker bridge network (Grafana reaches Prometheus, Tempo and
  Loki by name); host-mode Alloy reaches them through the published ports above.
- Docker Desktop (Windows/macOS) needs host networking enabled (4.34+) to use these files
  locally; otherwise run the services with `gradlew bootRun`.
- Run a service either as a container or with `bootRun`, not both; they share the port.

## Testing over plain HTTP on a LAN

No code or YAML edits are needed; set the same LAN address in the `.env` files (see the
`<LAN-IP>` blocks in each `.env.example`):

- **Every backend service:** `AUTH_ISSUER_URI=http://<LAN-IP>:9000` (must equal the token `iss`
  and the address the browser uses), and `CORS_ALLOWED_ORIGINS=http://<LAN-IP>:5173,http://<LAN-IP>:3000`
  on auth, chat, content, gateway and media.
- **auth-service:** `CMS_ADMIN_REDIRECT_URIS` and `CMS_ADMIN_POST_LOGOUT_REDIRECT_URIS` for the admin origin.
- **admin-app / website:** their `VITE_*` / `NEXT_PUBLIC_*` values are read at build time, so rebuild
  after changing them.
- Open inbound TCP 5173 (or the admin port), 3000, 8080 and 9000 in the firewall.
- Over plain HTTP `crypto.subtle` is unavailable; the admin's PKCE helper falls back to a JS SHA-256.
- Restart all services after changing any value. HTTP is for testing only; production uses HTTPS.
