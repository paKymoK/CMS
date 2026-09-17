---
id: anti-pattern.overly-broad-catch-block
category: convention
severity: medium
applies_to: try/catch blocks
---

# Catching a wider exception type than the code actually expects

## Why it matters

This is a different problem from the swallowed-exception rule — a catch block can log the exception
perfectly and still be wrong if the *type* it catches is broader than what the try block can
actually throw for expected reasons. `catch (Exception e)` around code that's only supposed to
throw a specific checked exception also silently catches `NullPointerException`,
`ClassCastException`, `IllegalStateException` — real bugs — and handles them identically to the
expected failure case (a retry, a fallback value, a generic error response). The bug gets masked as
routine, expected behavior instead of surfacing as the programming error it actually is.

## Bad example

```java
try {
  int amount = Integer.parseInt(request.getAmount());
  processPayment(amount, computeFee(request)); // could NPE if request fields are unexpectedly null
} catch (Exception e) { // catches the intended NumberFormatException AND any real bug above it
  log.warn("Invalid amount, using default: {}", e.getMessage());
  processPayment(DEFAULT_AMOUNT, 0);
}
```

## Good example

```java
try {
  int amount = Integer.parseInt(request.getAmount());
  processPayment(amount, computeFee(request));
} catch (NumberFormatException e) { // only the specific, expected failure is handled
  log.warn("Invalid amount, using default: {}", e.getMessage());
  processPayment(DEFAULT_AMOUNT, 0);
}
```

## Detection heuristic

- `catch (Exception e)` (or `Throwable`) wrapping a try block that contains a narrow, well-known
  parsing/conversion call — `Integer.parseInt`, `Long.parseLong`, `Double.parseDouble`,
  `UUID.fromString`, `LocalDate.parse`, etc. — followed by one or more *additional* method calls is
  the single clearest trigger on its own. Flag it purely from that shape, regardless of whether the
  catch block logs `e`, `e.getMessage()`, or nothing at all — logging presence answers the
  swallowed-exception question, not this one; a catch type broader than the parsing call alone
  justifies is wrong either way.
- `catch (Exception e)` or `catch (Throwable t)` around a block that only calls methods declaring
  specific checked exceptions, or none at all — the broad catch is doing more work than the block
  needs.
- A catch block whose recovery logic (default value, retry, generic error) makes sense for one
  specific failure mode but would silently apply to any unrelated exception too.
- Distinguish from the swallowed-exception rule: that one is about *not logging* what was caught;
  this one is about catching *more than intended* even when logging is done correctly — both can
  apply to the same block at once.
