---
id: anti-pattern.inefficient-collection-lookup
category: performance
severity: low
applies_to: repeated membership checks or lookups against a List
---

# Using a List where lookup cost should be O(1)

## Why it matters

`List.contains()`, or a manual loop searching for a match, is O(n) per call — fine once, costly
when done repeatedly against the same collection (inside another loop, or once per request against
a list that's rebuilt or reused often). Swapping the collection type to a `HashSet` (for membership
checks) or `HashMap` (for key-based lookup) turns the same logic into O(1) per call with no
behavior change, just by picking a structure suited to how the data is actually accessed.

## Bad example

```java
List<String> blockedIds = fetchBlockedIds(); // grows over time
for (User user : users) {
  if (blockedIds.contains(user.getId())) { // O(n) check, run once per user => O(n*m)
    continue;
  }
  ...
}
```

## Good example

```java
Set<String> blockedIds = new HashSet<>(fetchBlockedIds());
for (User user : users) {
  if (blockedIds.contains(user.getId())) { // O(1) check
    continue;
  }
  ...
}
```

## Detection heuristic

- A diff line calling `.contains(...)` (or `.indexOf(...)`) on a variable/parameter declared as
  `List<...>`, where that call sits inside a `for`/`forEach`/stream loop — this exact shape,
  `List`-typed variable + `.contains` + inside a loop, is the trigger by itself. Flag it from the
  type declaration and call site alone; don't require separate evidence the list is large.
- `list.contains(...)` or `list.indexOf(...)` called inside a loop, especially a loop over another
  collection of comparable or larger size — the nested cost is what makes this worth flagging, not
  a single standalone `contains` call.
- A `List` used purely for membership testing or key-based lookup (never accessed by index, never
  relies on ordering or duplicates) — a strong signal it should be a `Set`/`Map` instead.
- Don't flag small, fixed-size lists (a handful of enum-like constants) — the cost only matters when
  the list scales with request volume or dataset size.
