---
id: anti-pattern.broken-object-level-authorization
category: security
severity: high
applies_to: any endpoint that takes a resource ID (path/query/body param)
---

# Endpoint trusts "authenticated" instead of checking "authorized for this resource"

## Why it matters

An endpoint that takes an ID (`userId`, `orderId`, `conversationId`, ...) and fetches or mutates
that resource is not safe just because a valid JWT/session is required. Being logged in proves
*who* the caller is, not that they're allowed to touch *that specific* resource. Without an
explicit ownership/membership check, any authenticated user can enumerate IDs and read or modify
other users' data — this is OWASP's #1 API vulnerability class (Broken Object Level Authorization)
because it's so easy to write correctly-compiling, seemingly-working code that has the hole.

## Bad example

```java
@GetMapping("/employees/{sub}/personal")
public Mono<EmployeePersonal> getPersonal(@PathVariable String sub) {
  return employeeService.getPersonal(sub); // any authenticated caller, any sub
}
```

Compiles fine, works in a demo, and silently lets any logged-in user read any other employee's
national ID, address, and emergency contacts by changing the path parameter.

## Good example

```java
@GetMapping("/employees/{sub}/personal")
public Mono<EmployeePersonal> getPersonal(@PathVariable String sub, Authentication auth) {
  return accessGuard.requireSelfOrAdmin(auth, sub)
      .then(employeeService.getPersonal(sub));
}
```

The fix isn't "add auth" (it's already authenticated) — it's checking that the caller's identity
matches, owns, or is otherwise permitted against the *specific* `sub`/ID being requested.

## Detection heuristic

- A `@GetMapping`/`@PostMapping`/etc. with a path, query, or body parameter that identifies a
  resource (an ID, a `sub`, a slug), where the method body goes straight to a repository/service
  call with no comparison against the authenticated principal.
- Look for the class's own sibling methods: if some methods in the same controller call a
  `requireSelfOrAdmin`/`requireOwner`/`assertParticipant`-style guard and this one doesn't, that
  inconsistency is the strongest signal — the pattern exists, it just wasn't applied here.
- Especially high severity when the returned/mutated resource contains PII, financial data, or
  another user's private content.
