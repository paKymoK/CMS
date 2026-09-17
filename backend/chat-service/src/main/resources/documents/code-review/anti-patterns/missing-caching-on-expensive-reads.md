---
id: anti-pattern.missing-caching-on-expensive-reads
category: performance
severity: low
applies_to: read-heavy endpoints backed by rarely-changing or expensive-to-compute data
---

# Recomputing or refetching the same expensive result on every call

## Why it matters

Data that's expensive to produce (a heavy query, an aggregation, a call to a slow external
service) but changes rarely or has a natural staleness tolerance (a config list, a lookup table, a
per-user permission set recalculated every request) is a good caching candidate. Recomputing it on
every single call spends real latency and load on work whose answer was almost certainly the same
a second ago. This isn't "always cache everything" — it's specifically about read paths where the
cost is clearly disproportionate to how often the underlying data actually changes.

## Bad example

```java
@GetMapping("/reference-data/countries")
public Mono<List<Country>> getCountries() {
  return countryRepository.findAll().collectList(); // same ~250 rows, queried on every call
}
```

## Good example

```java
@Cacheable("countries")
@GetMapping("/reference-data/countries")
public Mono<List<Country>> getCountries() {
  return countryRepository.findAll().collectList();
}
```

## Detection heuristic

- An endpoint or service method that queries or computes something clearly static or slow-changing
  (reference/lookup data, aggregated stats, a third-party rate/config fetch) with no cache
  (`@Cacheable`, Redis, in-memory) anywhere in the call path.
- A value fetched fresh inside a hot loop or on every request when the code already has a caching
  layer available elsewhere in the project (Redis is already a dependency here) that just wasn't
  reached for.
- Don't flag data that's expected to be strongly consistent per-request (account balance, current
  auth state, anything where staleness would be a correctness bug, not just a performance one).
