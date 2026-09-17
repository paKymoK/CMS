---
id: anti-pattern.sensitive-data-in-logs
category: security
severity: medium
applies_to: logging statements (log.info/debug/error, printStackTrace)
---

# Logging a credential, token, or PII

## Why it matters

Log files are read by more people and systems than the original request ever was — shipped to
aggregators, retained far longer than a session, and often accessible to a broader on-call/ops
audience than the data's owner would expect. A password, bearer token, API key, or piece of PII
(national ID, full card number, precise location) written to a log at `info`/`debug` level — even
"just for debugging," even just once — persists in every downstream system that log line reaches,
and is easy to forget about since it doesn't fail any test.

## Bad example

```java
log.info("Login attempt: user={}, password={}", username, password);
log.debug("Authenticated with token: {}", jwt);
```

## Good example

```java
log.info("Login attempt: user={}", username);
log.debug("Authenticated, subject={}", jwt.getSubject()); // claim, not the raw token
```

## Detection heuristic

- A log statement whose arguments include a variable named/typed like a password, token, secret,
  or API key, or an entire request/response object that might contain one as a field.
- `e.printStackTrace()` or logging a full exception in a path that could include request
  parameters — check whether the exception message embeds sensitive input.
- Logging an entire DTO/entity (`log.info("request={}", req)`) where that type has a field holding
  a credential or PII — the leak happens the moment a new sensitive field is added to that type,
  not at the log call site itself, which is what makes this easy to miss in review.
