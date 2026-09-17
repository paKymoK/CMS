---
id: anti-pattern.auth-endpoint-without-rate-limiting
category: security
severity: medium
applies_to: login, token issuance, password reset, OTP/2FA verification endpoints
---

# New credential-checking endpoint with no throttling

## Why it matters

Any endpoint that checks a credential against a stored value — login, token exchange, password
reset, OTP verification — is a brute-force target: without a limit on attempts per account/IP/time
window, an attacker can simply guess at whatever rate the server allows. This doesn't need to be
sophisticated; a basic loop against an unthrottled login endpoint is enough to make weak passwords
or short OTP codes crackable in a practical amount of time. It's easy to miss in review because the
endpoint works correctly for every legitimate single request — the absence only matters under
repeated abuse, which a functional test won't exercise.

## Bad example

```java
@PostMapping("/login")
public Mono<TokenResponse> login(@RequestBody LoginRequest req) {
  return authService.authenticate(req.getUsername(), req.getPassword()); // unlimited attempts
}
```

## Good example

```java
@PostMapping("/login")
public Mono<TokenResponse> login(@RequestBody LoginRequest req) {
  return rateLimiter.checkAndConsume(req.getUsername()) // e.g. bucket4j/resilience4j, per-account + per-IP
      .then(authService.authenticate(req.getUsername(), req.getPassword()));
}
```

## Detection heuristic

- A new or modified endpoint whose purpose is verifying a password, OTP, invite code, or token,
  added without any rate-limiter/bucket check in the chain.
- No lockout, backoff, or CAPTCHA escalation after repeated failures for the same account or
  source IP.
- Check whether a rate-limiting library or filter already exists elsewhere in the service — if so,
  a new auth endpoint that doesn't use it is inconsistent with established practice, not just
  missing a feature from scratch.
