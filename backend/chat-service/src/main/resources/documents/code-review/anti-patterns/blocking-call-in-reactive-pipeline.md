---
id: anti-pattern.blocking-call-in-reactive-pipeline
category: performance
severity: high
applies_to: WebFlux / Project Reactor (Mono, Flux)
---

# Blocking call inside a reactive pipeline

## Why it matters

This platform runs on WebFlux with a small, fixed pool of event-loop threads (Netty). Every
`Mono`/`Flux` chain is expected to run on those threads. A single blocking call — a JDBC query, a
synchronous HTTP client, `Thread.sleep`, `Future.get()`, or `.block()` on another reactive chain —
stalls one of those threads for the duration of the call. Because the pool is small, a handful of
blocked threads is enough to stall unrelated requests across the whole service, not just the one
that made the blocking call. This fails silently under light load and shows up as latency spikes or
timeouts under real traffic.

## Bad example

```java
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
  private final JdbcTemplate jdbcTemplate; // blocking driver

  public Mono<ReportSummary> summarize(UUID reportId) {
    return Mono.fromCallable(() -> jdbcTemplate.queryForObject(
            "SELECT * FROM report WHERE id = ?", ReportSummary.class, reportId))
        // fromCallable alone does NOT move this off the event loop —
        // it still runs the blocking call on whatever thread subscribes.
        ;
  }
}
```

Also watch for a `.block()` call added "just to get the value out" inside a method that otherwise
returns `Mono`/`Flux` — this defeats the whole reactive chain and blocks whichever thread called it.

## Good example

```java
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
  private final JdbcTemplate jdbcTemplate;

  public Mono<ReportSummary> summarize(UUID reportId) {
    return Mono.fromCallable(() -> jdbcTemplate.queryForObject(
            "SELECT * FROM report WHERE id = ?", ReportSummary.class, reportId))
        .subscribeOn(Schedulers.boundedElastic()); // isolate the blocking call
  }
}
```

Prefer a non-blocking driver in the first place (R2DBC, `WebClient`) over isolating a blocking one;
`subscribeOn(boundedElastic())` is a mitigation for calls you can't avoid (legacy libraries,
`ProcessBuilder`, blocking SDKs), not a substitute for using the reactive stack correctly.

## Detection heuristic

- Any `spring-boot-starter-data-jdbc` repository, `JdbcTemplate`, or JPA `EntityManager` used from a
  class that also returns `Mono`/`Flux` — confirm the call site is wrapped in
  `Mono.fromCallable(...).subscribeOn(Schedulers.boundedElastic())`.
- `.block()`, `.blockFirst()`, `.blockLast()` anywhere outside test code or a `main` method.
- `Thread.sleep`, `Future.get()`, `CompletableFuture.join()` inside a reactive call chain.
- A method that mixes an R2DBC repository call and a `data-jdbc` repository call for the same
  entity — a strong signal the blocking one was a shortcut.

## Note for this codebase

`chat-service` depends on both `spring-boot-starter-data-r2dbc` and `spring-boot-starter-data-jdbc`
(see `build.gradle`) — confirm which repositories use which, and that any `data-jdbc` usage is
intentionally isolated (e.g. a batch/reader-writer path, as in `AttachmentListReader`/
`AttachmentListWriter`) rather than called from a request-handling reactive chain.
