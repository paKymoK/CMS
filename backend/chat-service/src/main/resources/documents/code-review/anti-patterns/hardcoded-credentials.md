---
id: anti-pattern.hardcoded-credentials
category: security
severity: high
applies_to: source files and config (YAML/properties)
---

# Secret literal committed in source or config

## Why it matters

Any credential — a client secret, DB/LDAP password, API key, signing key — written as a literal
string in a `.java` file or a `.yml`/`.properties` file becomes permanent the moment it's
committed: it's in git history forever, visible to anyone with repo read access, and shows up in
every clone and CI log that touches the file. Rotating it later doesn't undo the exposure. This is
different from a *test/throwaway* value used only in local dev — the risk is a real, working
credential (or something structured to look like production config) checked into version control
instead of pulled from an environment variable or secret manager at runtime.

## Bad example

```java
private static final String CLIENT_SECRET = "{noop}workflow-secret";
```

```yaml
ldap:
  password: admin_secret
```

## Good example

```java
@Value("${oauth.client-secret}")
private String clientSecret;
```

```yaml
ldap:
  password: ${LDAP_BIND_PASSWORD:}
```

## Detection heuristic

- A field or YAML value assigned a string literal that looks like a secret — named `secret`,
  `password`, `apiKey`, `token`, `credential`, or similar — with no `${ENV_VAR}` placeholder.
- Special attention to `{noop}`-prefixed Spring Security passwords: `{noop}` means "compare as
  plaintext," so a `{noop}` literal is a working, unencoded credential, not just a placeholder.
- A newly-added client/service registration (OAuth client, DB connection, third-party API) that
  copies an existing hardcoded-secret pattern instead of the env-var one used elsewhere in the same
  file — inconsistency with a sibling config value is a strong signal.
