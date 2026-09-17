---
id: anti-pattern.sequential-calls-that-could-be-parallel
category: performance
severity: medium
applies_to: multiple independent I/O calls (HTTP, DB, cache) in the same method
---

# Independent calls awaited one after another instead of concurrently

## Why it matters

When a method makes two or more calls that don't depend on each other's results — fetching a
user's profile and their settings, calling two unrelated downstream services — awaiting them
sequentially means the total latency is the *sum* of each call instead of the *max*. This is a
pure latency cost with no correctness benefit: the calls would return the same results either way,
just slower. It's easy to write sequentially by default (it reads top-to-bottom, like the rest of
the method) and easy to miss in review unless someone checks whether call #2 actually needs
anything call #1 returned.

## Bad example

```java
public Mono<Dashboard> load(String userId) {
  return profileClient.getProfile(userId)
      .flatMap(profile -> settingsClient.getSettings(userId) // waits for profile first, unnecessarily
          .map(settings -> new Dashboard(profile, settings)));
}
```

## Good example

```java
public Mono<Dashboard> load(String userId) {
  return Mono.zip(profileClient.getProfile(userId), settingsClient.getSettings(userId))
      .map(t -> new Dashboard(t.getT1(), t.getT2())); // both fire concurrently
}
```

## Detection heuristic

- Two or more `flatMap`/`await`/blocking calls chained one after another where the second call's
  arguments don't reference anything the first call returned.
- A method that reads like "step 1, step 2, step 3" against unrelated services/repositories with no
  data dependency between the steps — check whether `Mono.zip`/`CompletableFuture.allOf`/parallel
  streams would produce the same result faster.
- Especially worth flagging when at least one of the calls is a known-slow external service
  (another microservice, a third-party API) — the latency cost is most visible there.
