---
id: anti-pattern.deep-nesting-instead-of-guard-clauses
category: convention
severity: low
applies_to: methods with multiple nested if/else levels
---

# Nested conditionals instead of early returns

## Why it matters

A method that wraps its real logic in several levels of nested `if`/`else` makes the reader track
multiple open conditions at once to figure out which branch they're in and why. The same logic
expressed as guard clauses — handle each invalid/edge case first with an early `return`, leaving
the main path unindented at the top level — reads linearly and makes the "normal" case obvious
instead of buried three levels deep. This is a readability/maintainability concern, not a
correctness one: both versions behave identically, but one is much easier to review and modify
safely later.

## Bad example

```java
public Mono<Order> process(Order order) {
  if (order != null) {
    if (order.getStatus() == Status.PENDING) {
      if (order.getItems() != null && !order.getItems().isEmpty()) {
        return orderService.submit(order);
      } else {
        return Mono.error(new IllegalArgumentException("No items"));
      }
    } else {
      return Mono.error(new IllegalStateException("Not pending"));
    }
  } else {
    return Mono.error(new IllegalArgumentException("Order required"));
  }
}
```

## Good example

```java
public Mono<Order> process(Order order) {
  if (order == null) return Mono.error(new IllegalArgumentException("Order required"));
  if (order.getStatus() != Status.PENDING) return Mono.error(new IllegalStateException("Not pending"));
  if (order.getItems() == null || order.getItems().isEmpty()) return Mono.error(new IllegalArgumentException("No items"));

  return orderService.submit(order);
}
```

## Detection heuristic

- Three or more nested `if`/`else` levels in a single method where each level is really an
  independent precondition check, not a genuinely interdependent decision tree.
- An `else` branch that only exists to return an error/short-circuit — that's a sign the condition
  should be inverted into an early return instead of wrapping the rest of the method.
- Don't flag genuine branching logic where multiple outcomes are all equally "normal" (a
  state-machine dispatch, a strategy selection) — this rule is about accidental nesting from
  precondition checks, not all conditionals in general.
