# Local LDAP — Dev Setup

OpenLDAP + phpLDAPadmin running in Docker for local development.

## Start

```bash
docker-compose up -d
```

## Stop

```bash
docker-compose down -v
```

> `-v` removes the volume so the next `up` re-seeds cleanly from `seed.ldif`.

---

## Endpoints

| Service | URL |
|---------|-----|
| LDAP | `ldap://localhost:389` |
| phpLDAPadmin UI | http://localhost:8090 |

**phpLDAPadmin login**
- Login DN: `cn=admin,dc=mycompany,dc=local`
- Password: `admin_secret`

---

## Seeded Users

All users have the default password: **`123456`**

### Internal Users (`ou=users`)

| CN | UID (login username) | Mail |
|----|----------------------|------|
| john.doe | `john.doe` | john.doe@mycompany.local |
| jane.smith | `jane.smith` | jane.smith@mycompany.local |

> Login only relies on `cn`/username + password — no LDAP groups are used for
> authentication or authorization. Roles/permissions are managed separately in
> Postgres (`authorities`, `client_role_assignment`, `user_group*`).

---

## Auth-service config

Make sure `application.yaml` (or env vars) matches this setup:

```yaml
ldap:
  url: ldap://localhost:389
  base: dc=mycompany,dc=local
  username: cn=admin,dc=mycompany,dc=local
  password: admin_secret
  user-search-base: ou=users
  user-search-filter: "(cn={0})"
```

## Re-seeding

If you need a clean slate (e.g. after editing `seed.ldif`):

```bash
docker-compose down -v && docker-compose up -d
```
