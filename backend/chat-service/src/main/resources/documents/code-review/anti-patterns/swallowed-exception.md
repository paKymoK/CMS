---
id: anti-pattern.swallowed-exception
category: convention
severity: medium
applies_to: exception handling, general Java
---

# Swallowing an exception instead of surfacing it

## Why it matters

`catch (Exception e)` that logs nothing (or logs and then silently returns a default/empty value)
destroys the information needed to debug the failure later. The caller sees normal-looking output
— an empty list, a `null`, an unauthorized response — with no trace of *why*. This is especially
costly in security- and auth-adjacent code, where a swallowed exception can silently turn "the
token was malformed" and "the user genuinely lacks access" into the same indistinguishable outcome.

Catching broadly is sometimes correct (a best-effort fallback, a health check that must not throw)
— the anti-pattern is doing it *without logging the exception itself*, only a hardcoded message or
nothing at all.

## Real instances in this codebase, contrasted

Loses the actual cause — logs a fixed string, not `e`:

```java
// core-v1/src/main/java/com/takypok/core/util/AuthenticationUtil.java
private static Mono<Void> setUnauthorized(ServerHttpResponse response) {
  ...
  try {
    ...
    return response.writeWith(Mono.just(responseBuffer));
  } catch (Exception ignored) {
    return Mono.empty();
  }
}
```

If `objectMapper.writeValueAsString(...)` or `response.writeWith(...)` throws here, the request
just gets no response at all, with zero trace in the logs of what went wrong.

Preserves the cause even in a best-effort catch:

```java
// chat-service/src/main/java/com/takypok/chatservice/service/ChatSessionRegistry.java
private String serialize(Object value) {
  try {
    return objectMapper.writeValueAsString(value);
  } catch (Exception e) {
    log.error("Unable to serialize chat payload: {}", e.getMessage(), e);
    return null;
  }
}
```

Same shape — catch broadly, degrade gracefully — but the exception is actually logged with its
stack trace, so a real failure is diagnosable instead of invisible.

## Detection heuristic

- `catch (Exception ignored)` / `catch (Exception e) {}` with no logging statement at all.
- A catch block whose log call doesn't include the caught variable (`e`/`ex`) — logging a static
  string instead of the exception loses the stack trace.
- A catch block in an auth/security code path (token validation, permission checks) that returns a
  generic "unauthorized"/`false`/`empty` without distinguishing "explicitly denied" from
  "unexpected internal error" in the log.
