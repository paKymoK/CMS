---
id: anti-pattern.optional-and-null-misuse
category: convention
severity: medium
applies_to: general Java, method signatures
---

# Optional/null misuse

## Why it matters

`Optional` exists to make "this might not have a value" visible in a method's *return type* so
callers are forced to handle it. Two opposite misuses defeat that purpose: calling `.get()` on an
`Optional` without checking it first (turns "absent" into an unchecked exception at the call site,
no better than a raw `null` dereference), and using `Optional` as a field type, constructor
parameter, or method parameter (it isn't serializable, adds no safety there, and just adds
indirection the caller has to unwrap immediately).

## Bad example

```java
public Conversation getConversation(UUID id) {
  Optional<Conversation> found = conversationRepository.findById(id);
  return found.get(); // throws NoSuchElementException with no context if absent
}

public class ConversationRequest {
  private Optional<String> title; // Optional as a field — awkward to construct, not serializable
}
```

## Good example

```java
public Mono<Conversation> getConversation(UUID id) {
  return conversationRepository.findById(id)
      .switchIfEmpty(Mono.error(new NotFoundException("Conversation not found: " + id)));
}

public class ConversationRequest {
  private String title; // nullable field; absence means "not provided", checked where it matters
}
```

In a reactive codebase, `Mono.empty()` / `Mono.error(...)` is the equivalent of `Optional` for a
single value — prefer `switchIfEmpty`/`defaultIfEmpty` over ever unwrapping an `Optional` with
`.get()` inside a reactive chain.

## Detection heuristic

- `.get()` called on an `Optional` without a preceding `.isPresent()`/`.isEmpty()` check,
  `.orElseThrow(...)`, or equivalent.
- `Optional<T>` used as a class field type, constructor parameter, or method parameter (as opposed
  to a method *return* type, which is the intended use).
- A method that returns `null` for "not found" right next to another method in the same class that
  returns `Optional`/`Mono.empty()` for the same situation — inconsistent absence handling within
  one class.
