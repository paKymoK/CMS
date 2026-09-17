---
id: anti-pattern.reactor-multiple-subscription-side-effects
category: performance
severity: high
applies_to: WebFlux / Project Reactor (Mono, Flux)
---

# Re-subscribing to a Mono/Flux that has side effects

## Why it matters

A `Mono`/`Flux` is a *description* of work, not a value. Nothing happens until something
subscribes to it, and — unless explicitly cached — every subscription re-runs the whole chain from
scratch, including any side effects (a DB write, an outbound HTTP call, a Kafka publish). Code that
looks like it's "reusing a result" by holding a `Mono` in a local variable and using it twice is
actually issuing the underlying operation twice. This is a common source of duplicate writes,
duplicate emails/notifications, or doubled external calls that only becomes visible once the code
runs under conditions that actually trigger both subscriptions (e.g. a retry, or two different
branches of an `if`).

## Bad example

```java
public Mono<MessageResponse> sendAndNotify(SendMessageRequest request, EmployeeDirectory sender) {
  Mono<Message> saved = messageRepository.save(toEntity(request, sender)); // not yet executed

  if (request.isUrgent()) {
    saved.flatMap(this::pushUrgentNotification).subscribe(); // subscription #1 — insert happens
  }
  return saved.map(this::toResponse); // subscription #2 — insert happens AGAIN, a duplicate row
}
```

## Good example

```java
public Mono<MessageResponse> sendAndNotify(SendMessageRequest request, EmployeeDirectory sender) {
  return messageRepository.save(toEntity(request, sender))
      .flatMap(message -> {
        Mono<Message> notify = request.isUrgent()
            ? pushUrgentNotification(message).thenReturn(message)
            : Mono.just(message);
        return notify;
      })
      .map(this::toResponse); // single subscription, one insert
}
```

If a value genuinely must be reused across independent subscribers, cache it explicitly and only
after understanding the tradeoff (`.cache()` retains the emitted value/error for replay — it does
not "cache and share progress" like a shared `Future`, and it must not be used for a `Mono` whose
side effect (like a DB write) you actually want to run more than once):

```java
Mono<Message> saved = messageRepository.save(toEntity(request, sender)).cache();
```

## Detection heuristic

- A `Mono`/`Flux` stored in a local variable and referenced more than once (`.subscribe()` in one
  branch and `return`ed or `.map`'d in another, or used in both an `if` and the code after it).
- `.subscribe()` called explicitly inside a method that also returns a `Mono`/`Flux` — a strong
  signal of an accidental second subscription alongside the one the caller will trigger.
- Any repository `.save(...)` / `.insert(...)` call assigned to a variable and consumed from more
  than one place.
