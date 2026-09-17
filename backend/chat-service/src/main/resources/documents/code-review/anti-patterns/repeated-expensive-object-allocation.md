---
id: anti-pattern.repeated-expensive-object-allocation
category: performance
severity: low
applies_to: general Java, Jackson ObjectMapper and similar heavyweight singletons
---

# Recreating an expensive, stateless object on every call

## Why it matters

Some objects are expensive to construct and designed to be built once and reused: `ObjectMapper`,
`Pattern` (from `Pattern.compile`), thread pools, HTTP clients. `ObjectMapper` in particular does
internal classpath scanning and module registration at construction time — building a fresh one
per request or per call adds avoidable CPU and GC pressure that scales with traffic, for no
behavioral benefit, since the object is thread-safe and holds no per-call state.

This is a slow-burn issue, not a crash: it doesn't show up in a quick manual test, only as elevated
CPU/GC time under real load, which makes it easy to miss without deliberately looking for it.

## Real instances in this codebase

```java
// core-v1/src/main/java/com/takypok/core/util/AuthenticationUtil.java
private static Mono<Void> setUnauthorized(ServerHttpResponse response) {
  response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
  response.setStatusCode(HttpStatus.FORBIDDEN);
  ObjectMapper objectMapper = new ObjectMapper(); // built fresh on every rejected request
  ...
}
```

A new `ObjectMapper` here on every single unauthorized response — exactly the kind of call site
that gets hit hardest when something is misconfigured and requests start failing auth in bulk.

## Good example

Reuse a single shared instance — this codebase already has one:

```java
// core-v1/src/main/java/com/takypok/core/config/ConfigObjectMapper.java
public static ObjectMapper objectMapper() { ... } // module-configured, meant to be shared
```

```java
private static Mono<Void> setUnauthorized(ServerHttpResponse response) {
  response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
  response.setStatusCode(HttpStatus.FORBIDDEN);
  try {
    DataBuffer responseBuffer = response.bufferFactory().wrap(
        objectMapper() // shared instance from ConfigObjectMapper
            .writeValueAsString(new ResultMessage<>(new ResultStatus("403", "Unauthorized"), null))
            .getBytes(StandardCharsets.UTF_8));
    return response.writeWith(Mono.just(responseBuffer));
  } catch (Exception e) {
    log.error("Failed to write unauthorized response: {}", e.getMessage(), e);
    return Mono.empty();
  }
}
```

## Detection heuristic

- `new ObjectMapper()` (or `new Gson()`, `new XmlMapper()`, etc.) inside a method body rather than
  as a field/bean — especially inside a method that runs per-request.
- `Pattern.compile(...)` inside a method body instead of a `static final` field.
- Check whether the class or module already exposes a shared instance (as `ConfigObjectMapper`
  does here) before assuming a new one is needed — this specific case is a missed reuse of
  existing project infrastructure, not just a general Java tip.
