---
id: anti-pattern.magic-numbers-and-strings
category: convention
severity: low
applies_to: literals used for status codes, roles, event types, thresholds
---

# Unexplained literal instead of a named constant or enum

## Why it matters

A bare `"ADMIN"`, `3`, or `"PENDING"` scattered through the code carries no explanation of what it
means or where else it's used — a reader has to guess or go searching, and if the value ever needs
to change, every occurrence has to be found and updated by hand instead of in one place. This is
especially risky for values that represent a closed set of options (statuses, roles, event types):
a typo in one of the string literals (`"Pending"` vs `"PENDING"`) compiles fine and fails silently
at runtime instead of being caught by the compiler.

## Bad example

```java
if (user.getRole().equals("ADMIN")) { ... }
if (order.getStatus() == 3) { ... } // what is 3?
```

## Good example

```java
if (user.getRole() == Role.ADMIN) { ... }
if (order.getStatus() == OrderStatus.SHIPPED) { ... }
```

## Detection heuristic

- A string or numeric literal compared against a field that represents a status, role, type, or
  other closed set of options, where an enum or constant already exists (or clearly should) for
  that set.
- The same literal value repeated across multiple files/methods — if it needs to be typed
  correctly in more than one place, it should be defined once.
- A numeric threshold (timeout, retry count, page size) embedded inline with no named constant and
  no comment explaining the chosen value.
