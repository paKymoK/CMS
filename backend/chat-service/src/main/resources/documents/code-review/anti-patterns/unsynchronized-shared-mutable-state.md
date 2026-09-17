---
id: anti-pattern.unsynchronized-shared-mutable-state
category: performance
severity: high
applies_to: Spring singleton beans, concurrent access
---

# Unsynchronized mutable state in a singleton bean

## Why it matters

`@Component`/`@Service`/`@Bean` classes are singletons by default — one instance handles every
concurrent request. A plain `HashMap`, `ArrayList`, or non-atomic counter held as an instance field
on such a class is shared, mutable state accessed from many threads at once (Netty event-loop
threads under WebFlux, or the servlet container's thread pool). Concurrent, unsynchronized
mutation of a non-thread-safe collection can corrupt its internal structure (lost updates,
`ConcurrentModificationException`, or in the worst case an infinite loop inside `HashMap` under
concurrent resize) — a bug that is close to impossible to reproduce in a single-threaded manual
test and only appears under real concurrent load.

## Bad example

```java
@Component
public class SessionRegistry {
  // plain HashMap/HashSet shared across every concurrent caller of this singleton
  private final Map<String, Set<Sinks.Many<String>>> sinksBySub = new HashMap<>();

  public void register(String sub, Sinks.Many<String> sink) {
    sinksBySub.computeIfAbsent(sub, k -> new HashSet<>()).add(sink);
  }
}
```

## Good example — this codebase does this correctly

```java
// chat-service/src/main/java/com/takypok/chatservice/service/ChatSessionRegistry.java
@Component
@RequiredArgsConstructor
public class ChatSessionRegistry {
  private final Map<String, Set<Sinks.Many<String>>> sinksBySub = new ConcurrentHashMap<>();

  public void register(String sub, Sinks.Many<String> sink) {
    sinksBySub.computeIfAbsent(sub, key -> new CopyOnWriteArraySet<>()).add(sink);
  }

  public void unregister(String sub, Sinks.Many<String> sink) {
    sinksBySub.computeIfPresent(sub, (key, sinks) -> {
      sinks.remove(sink);
      return sinks.isEmpty() ? null : sinks;
    });
  }
}
```

`ConcurrentHashMap` for the outer map plus `CopyOnWriteArraySet` for the per-key set — a deliberate
choice matching the access pattern (many concurrent registers/unregisters, occasional iteration to
fan out an event). Use this class as the reference pattern for any new component that needs
per-instance, request-shared mutable state.

## Detection heuristic

- `private final Map`/`List`/`Set` field (not method-local) on a `@Component`/`@Service`/`@Bean`
  class, initialized with `HashMap`/`ArrayList`/`HashSet` rather than a concurrent collection.
- A non-`Atomic*` counter (`int`, `long`, boxed `Integer`) incremented/decremented as instance
  state on a singleton bean.
- Mutation of such a field from more than one method that could run concurrently (e.g. a
  register/unregister pair, or a read path racing a write path).
