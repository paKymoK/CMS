---
id: anti-pattern.weak-or-inconsistent-credential-hashing
category: security
severity: high
applies_to: password/secret storage and comparison
---

# Storing or comparing a credential without a proper one-way hash

## Why it matters

Passwords and similar credentials must be stored using a slow, salted one-way hash (bcrypt,
scrypt, or Argon2) — never plaintext, and never a fast general-purpose hash (MD5, SHA-1, unsalted
SHA-256), which is crackable at billions of guesses/second on commodity hardware. A codebase can
also drift into an inconsistent posture: user passwords hashed correctly with bcrypt, while a
different credential type (API client secrets, service-account passwords) added later uses no
hashing at all — the second class of secret becomes the easy target even though the first is fine.

## Bad example

```java
String secret = "{noop}" + request.getClientSecret(); // stored/compared as plaintext
```

```java
String hash = DigestUtils.md5Hex(password); // fast hash, no salt — crackable at scale
```

## Good example

```java
PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder(); // bcrypt by default
String hash = encoder.encode(rawSecret);
```

## Detection heuristic

- Any credential encoded/prefixed as `{noop}` (Spring Security's plaintext marker) outside of a
  clearly-labeled local/test-only profile.
- `MessageDigest.getInstance("MD5"/"SHA-1"/"SHA-256")`, `DigestUtils.md5Hex`, or manual hashing
  applied to a password/secret instead of a `PasswordEncoder`/bcrypt/Argon2 library call.
- A new credential type (client secrets, invite codes, API keys used as bearer auth) added with a
  different — weaker — storage scheme than the one already used for user passwords in the same
  service.
