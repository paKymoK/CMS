---
id: anti-pattern.mass-assignment
category: security
severity: high
applies_to: request-body binding onto entities/DTOs
---

# Binding a request body onto fields the client shouldn't control

## Why it matters

When a request DTO is bound directly onto (or is itself) a persistence entity that includes
privileged fields — `role`, `isAdmin`, `accountBalance`, `verified` — a client can set those fields
just by including them in the JSON body, even though the UI never exposes them. The endpoint was
only ever *intended* to let the user edit their display name or email, but nothing enforces that
intent at the binding layer. This is especially easy to introduce when a DTO is reused for both
"create" and "update" and gains a new sensitive field over time without the binding being
re-audited.

## Bad example

```java
@PutMapping("/users/{id}")
public Mono<User> update(@PathVariable String id, @RequestBody User user) {
  user.setId(id);
  return userRepository.save(user); // client-supplied `role`/`isAdmin` fields pass straight through
}
```

## Good example

```java
@PutMapping("/users/{id}")
public Mono<User> update(@PathVariable String id, @RequestBody UpdateUserRequest req) {
  return userRepository.findById(id)
      .map(user -> { user.setDisplayName(req.getDisplayName()); user.setEmail(req.getEmail()); return user; })
      .flatMap(userRepository::save); // only the intended fields are ever touched
}
```

## Detection heuristic

- A controller method whose `@RequestBody` type is the persistence entity itself, or a DTO that
  mirrors it field-for-field, rather than a narrower request-specific type.
- Any DTO/entity bound from a request body that contains a role, permission, ownership, balance, or
  verification-status field — check whether the handler explicitly ignores that field or whether it
  flows straight into a save/update call.
- A new field added to an existing entity that is now reachable through an update endpoint that
  wasn't reviewed for that field's sensitivity when the endpoint was first written.
