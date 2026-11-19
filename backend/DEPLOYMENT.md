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
