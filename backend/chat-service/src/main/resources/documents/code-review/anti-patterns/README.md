# Code-review rule docs

One topic per file, each self-contained with: why it matters, a bad example, a good example, and a
detection heuristic — the retrieval unit for the code-review RAG pipeline. The first batch (rows
marked "real") cited concrete files in this repo; the authentication/performance/convention batch
added later is deliberately written as common, framework-general Java/Spring patterns instead,
since this reviewer is also used against other codebases, not just this one.

| File | Category | Severity | Grounded in real code? |
|---|---|---|---|
| `blocking-call-in-reactive-pipeline.md` | performance | high | synthetic (flags real risk: repo mixes r2dbc + data-jdbc) |
| `swallowed-exception.md` | convention | medium | real — `AuthenticationUtil` (bad) vs `ChatSessionRegistry` (good) |
| `repeated-expensive-object-allocation.md` | performance | low | real — `AuthenticationUtil.setUnauthorized` |
| `reactor-multiple-subscription-side-effects.md` | performance | high | synthetic |
| `unsynchronized-shared-mutable-state.md` | performance | high | real good example — `ChatSessionRegistry` |
| `optional-and-null-misuse.md` | convention | medium | synthetic |
| `god-class-mixed-responsibilities.md` | convention | medium | real discussion case — `IngestionService` |
| `third-party-or-long-io-in-transactional.md` | performance | high | real — `UserServiceImpl.create`/`updateProfile` (Kafka publish inside `@Transactional`) |
| `broken-object-level-authorization.md` | security | high | generic (pattern matches a real gap once found in `EmployeeController`, written generically) |
| `hardcoded-credentials.md` | security | high | generic |
| `weak-or-inconsistent-credential-hashing.md` | security | high | generic |
| `jwt-validation-gaps.md` | security | high | generic |
| `mass-assignment.md` | security | high | generic |
| `trusting-client-supplied-identity.md` | security | high | generic |
| `sensitive-data-in-logs.md` | security | medium | generic |
| `auth-endpoint-without-rate-limiting.md` | security | medium | generic |
| `n-plus-one-query.md` | performance | high | generic |
| `unbounded-result-set.md` | performance | medium | generic |
| `sequential-calls-that-could-be-parallel.md` | performance | medium | generic |
| `missing-caching-on-expensive-reads.md` | performance | low | generic |
| `inefficient-collection-lookup.md` | performance | low | generic |
| `magic-numbers-and-strings.md` | convention | low | generic |
| `overly-broad-catch-block.md` | convention | medium | generic |
| `leaking-entity-as-api-response.md` | convention | medium | generic |
| `deep-nesting-instead-of-guard-clauses.md` | convention | low | generic |

`process-stream-deadlock.md` was removed: it was grounded in `IngestionService`'s Python-subprocess
delegation for `.docx/.xlsx/.pptx`, which was dropped entirely — the risk no longer exists in this
codebase.

## Context-size warning

`CodeReviewService` loads every file in this directory into the prompt on every request (no
retrieval/similarity search — see the class-level comment there for the original rationale). That
was a reasonable call at 8 rules; at 25 rules the rule corpus alone is ~7.8k words (roughly
10-11k tokens), which already exceeds `num-ctx: 8192` in `application.yaml` before the system
prompt or the diff being reviewed are added. This needs to be revisited — either raise `num-ctx`
substantially, or switch to real retrieval (embed each rule doc, fetch only the top-k relevant to
the diff) — before relying on results from the current rule set.
