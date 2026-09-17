# Test case: multi-file diff bundling 6 distinct issues across 3 files

Not a rule doc — lives outside `anti-patterns/` on purpose so `CodeReviewService`'s
`classpath:documents/code-review/anti-patterns/*.md` glob never picks it up as a rule.

Used to check whether the reviewer handles a realistic, multi-file diff without issues in one
file interfering with detection in another — as opposed to the single-issue presets in
`CodeReviewPage.tsx`, which only ever exercise one rule at a time.

## How to run it

POST the diff body below as `text/plain` to `/code-review` (through the gateway:
`http://localhost:8080/chat-service/code-review`, authenticated), or paste it into the
"Paste diff" tab on `/assistant/code-review`.

## Diff

```diff
diff --git a/src/main/java/com/example/account/AccountController.java b/src/main/java/com/example/account/AccountController.java
index 1111111..2222222 100644
--- a/src/main/java/com/example/account/AccountController.java
+++ b/src/main/java/com/example/account/AccountController.java
@@ -10,4 +10,14 @@ public class AccountController {
   private final AccountService accountService;
+
+  private static final String INTERNAL_API_KEY = "sk_live_9f8a7b6c5d4e3f2a1b0c";
+
+  @GetMapping("/{accountId}/statement")
+  public Mono<Statement> getStatement(@PathVariable String accountId) {
+    return accountService.getStatement(accountId, INTERNAL_API_KEY);
+  }
 }

diff --git a/src/main/java/com/example/account/AccountReportService.java b/src/main/java/com/example/account/AccountReportService.java
index 1111111..2222222 100644
--- a/src/main/java/com/example/account/AccountReportService.java
+++ b/src/main/java/com/example/account/AccountReportService.java
@@ -14,6 +14,14 @@ public class AccountReportService {
   private final JdbcTemplate jdbcTemplate;

   public List<AccountSummary> loadSummaries(List<String> accountIds) {
+    List<AccountSummary> summaries = new ArrayList<>();
+    for (String id : accountIds) {
+      AccountSummary summary = jdbcTemplate.queryForObject(
+          "SELECT * FROM account WHERE id = ?", AccountSummary.class, id);
+      summaries.add(summary);
+    }
+    return summaries;
-    return List.of();
   }
 }

diff --git a/src/main/java/com/example/account/AccountEventPublisher.java b/src/main/java/com/example/account/AccountEventPublisher.java
index 1111111..2222222 100644
--- a/src/main/java/com/example/account/AccountEventPublisher.java
+++ b/src/main/java/com/example/account/AccountEventPublisher.java
@@ -12,9 +12,20 @@ public class AccountEventPublisher {
   public void publish(AccountEvent event) {
+    try {
+      if (event != null) {
+        if (event.getType() == 2) {
+          kafkaTemplate.send("account-events", event);
+        } else {
+          log.debug("Skipping event");
+        }
+      } else {
+        log.debug("Null event");
+      }
+    } catch (Exception e) {
+      return;
+    }
   }
 }
```

## Expected findings (6)

| # | File | Rule | Why |
|---|---|---|---|
| 1 | `AccountController.java` | `anti-pattern.broken-object-level-authorization` | `getStatement(accountId)` takes an ID path param and fetches data with no ownership/auth check on the caller. |
| 2 | `AccountController.java` | `anti-pattern.hardcoded-credentials` | `INTERNAL_API_KEY` is a literal secret string in source. |
| 3 | `AccountReportService.java` | `anti-pattern.n-plus-one-query` | `jdbcTemplate.queryForObject(...)` called once per ID inside a `for` loop instead of one batched query. |
| 4 | `AccountEventPublisher.java` | `anti-pattern.swallowed-exception` | `catch (Exception e) { return; }` — no logging at all, the failure vanishes silently. |
| 5 | `AccountEventPublisher.java` | `anti-pattern.magic-numbers-and-strings` | `event.getType() == 2` — an unexplained literal compared against what should be an enum. |
| 6 | `AccountEventPublisher.java` | `anti-pattern.deep-nesting-instead-of-guard-clauses` | Three nested `if`/`else` levels for what are really independent precondition checks. |

## What qwen3.5:4b actually produced (for comparison)

Before the system-prompt fix (rambling reasoning visible in the output, response cut off mid-way,
never finished covering `AccountEventPublisher.java`): found #2 correctly; also cited
`reactor-multiple-subscription-side-effects` and `unsynchronized-shared-mutable-state`, both
misattributed/self-contradicted per the model's own visible reasoning.

After the system-prompt fix (clean output, no rambling, but stops early): found #2 correctly,
every time across repeated runs; consistently misattributed #4 as
`anti-pattern.third-party-or-long-io-in-transactional` instead of `swallowed-exception` (that rule
requires an `@Transactional` method, which isn't present anywhere in this diff); never reached
#1, #3, #5, #6.

Diagnosis at the time: this looked like a small-model capacity limit (exhaustively checking every
file + discriminating between many similar rules in a 25-rule context) rather than something more
prompt tuning would fix — worth re-running this exact test against a larger model to check.
