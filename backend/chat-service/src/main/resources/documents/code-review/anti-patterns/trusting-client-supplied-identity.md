---
id: anti-pattern.trusting-client-supplied-identity
category: security
severity: high
applies_to: any handler that needs to know "who is calling"
---

# Reading identity from the request instead of the authenticated principal

## Why it matters

Once a request is authenticated, the *only* trustworthy source for "who is this" is the value the
auth framework extracted from the verified token/session (`Authentication`/`Principal`) — never a
`userId` field the client typed into a request body, query string, or custom header. A client can
set those to anything; there's nothing stopping a request claiming to act as a different user
unless the server independently derives identity from the verified credential.

## Bad example

```java
@PostMapping("/orders")
public Mono<Order> create(@RequestBody CreateOrderRequest req) {
  return orderService.create(req.getUserId(), req.getItems()); // userId taken from the body
}
```

## Good example

```java
@PostMapping("/orders")
public Mono<Order> create(@RequestBody CreateOrderRequest req, Authentication auth) {
  String userId = ((Jwt) auth.getPrincipal()).getSubject(); // identity from the verified token
  return orderService.create(userId, req.getItems());
}
```

## Detection heuristic

- A request DTO with a `userId`/`sub`/`ownerId`/`accountId` field that's used for anything more
  than display, especially if it's passed to a service method that performs a write or an
  authorization decision.
- A handler that has an `Authentication`/`Principal` parameter available (or could easily add one)
  but instead reads the equivalent identity from the body/header — check whether the two could ever
  disagree, and what happens if they do.
- A `X-User-Id`-style custom header trusted without also being cross-checked against the verified
  session — legitimate only behind a trusted internal gateway that itself re-derives and overwrites
  the header, never end-to-end from an external client.
