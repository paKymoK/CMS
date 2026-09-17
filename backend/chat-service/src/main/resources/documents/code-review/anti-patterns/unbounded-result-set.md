---
id: anti-pattern.unbounded-result-set
category: performance
severity: medium
applies_to: list/search endpoints and their backing queries
---

# List endpoint with no limit on how much it can return

## Detection rule (check this first, before anything else in this doc)

Look at every new or changed `@GetMapping`/`@PostMapping`-style method that returns
`List<...>`/`Flux<...>`/`Collection<...>`. Read its parameter list and the repository call inside
it. If neither one contains `Pageable`, a `page`/`size` pair, or a `limit`/`cursor` parameter,
that is by itself enough to flag `anti-pattern.unbounded-result-set` — no further evidence is
required, and you do not need to know how large the underlying table actually is.

```java
@GetMapping
public Flux<Message> listAll(@RequestParam String conversationId) {
  return messageRepository.findByConversationId(conversationId); // <- no Pageable/page/size/limit anywhere here: flag it
}
```

## Why it matters

An endpoint that returns "all X" without a page size cap works fine while the table is small, then
becomes a slow query and a large response payload as data accumulates. This gets *worse over time
in production* even if the code never changes, purely because the dataset grows — by the time it's
noticed it's usually already a live incident, not caught in normal testing.

## Good example

```java
@GetMapping
public Flux<Message> listAll(@RequestParam String conversationId,
    @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
  return messageRepository.findByConversationId(conversationId, PageRequest.of(page, Math.min(size, 100)));
}
```

## Additional signals (secondary — the detection rule above is the primary check)

- A new list/search endpoint added without a `page`/`size` (or cursor) parameter, especially one
  querying a table that's expected to grow (messages, events, logs, transactions) rather than a
  small, effectively-static one (roles, categories).
- A `size`/`limit` parameter that *is* accepted from the client but isn't capped server-side —
  letting the caller request an arbitrarily large page defeats the purpose of paginating.
