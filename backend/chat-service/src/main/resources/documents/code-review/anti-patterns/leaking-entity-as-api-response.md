---
id: anti-pattern.leaking-entity-as-api-response
category: convention
severity: medium
applies_to: controller return types backed by JPA/R2DBC/JDBC entities
---

# Returning a persistence entity directly from an API endpoint

## Why it matters

A JPA/R2DBC entity is shaped for storage, not for the API contract: it tends to carry every column
(including ones that shouldn't be public — password hashes, internal flags, foreign keys to
unrelated data), and its shape is tied to the schema, so any migration/column rename becomes a
breaking API change even when the actual response the client needs didn't change. A DTO decouples
the two — the API contract stays stable and intentional even as the underlying table evolves, and
only the fields meant to be public are ever serialized.

## Bad example

```java
@GetMapping("/users/{id}")
public Mono<User> getUser(@PathVariable String id) { // User is the @Entity/@Table class
  return userRepository.findById(id); // whatever columns exist, including passwordHash, get serialized
}
```

## Good example

```java
@GetMapping("/users/{id}")
public Mono<UserResponse> getUser(@PathVariable String id) {
  return userRepository.findById(id).map(UserResponse::from); // only the intended fields
}
```

## Detection heuristic

- A controller method's return type (or the type inside its `Mono`/`Flux`) is annotated
  `@Entity`/`@Table`/`@Document`, or is a repository's persistence model, rather than a
  response-specific class.
- The entity has a field that's clearly internal-only (a password hash, an internal status enum, a
  raw foreign key ID meant to be resolved server-side) and there's no mapping step filtering it out
  before serialization.
- A new column added to an existing entity that's already returned directly from an endpoint — the
  new column becomes publicly exposed with zero review of whether it should be.
