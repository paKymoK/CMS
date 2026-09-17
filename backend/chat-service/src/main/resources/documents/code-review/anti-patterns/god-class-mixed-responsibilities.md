---
id: anti-pattern.god-class-mixed-responsibilities
category: convention
severity: medium
applies_to: service layer design
---

# A single class doing too many unrelated things

## Why it matters

A class that mixes several unrelated responsibilities — file-system traversal, format dispatch,
external process orchestration, and vector-store cleanup, say — is harder to test in isolation
(each test has to set up the whole class's dependencies), harder to change safely (a change for one
responsibility risks breaking an unrelated one sharing the same class), and harder for a reviewer to
reason about because the class has no single, nameable purpose. This is a design smell more than a
correctness bug: nothing crashes today, but every future change to the class costs more than it
should.

## Example worth watching in this codebase

`chat-service/src/main/java/com/takypok/chatservice/service/IngestionService.java` currently
combines: directory walking and application-folder resolution, per-file-type document parsing
(PDF vs. plain text/markdown), and vector-store mutation (delete-by-filter, full collection wipe).
None of these individually is wrong, and the class isn't unmanageable yet — but each new document
type or new deletion policy added here makes the class a little more of a shared bottleneck. If it
keeps growing (e.g. once AST-aware code chunking is added for the code-review knowledge base), it's
a candidate to split along those seams — a dedicated parser-selection component separate from the
folder-walking/orchestration logic.

## Detection heuristic

- A class whose public methods don't share an obvious common noun (e.g. one class exposing both
  "ingest a file" and "delete this application's vectors" and "list available applications").
- A class where more than ~half the imports belong to concerns unrelated to its name (e.g. an
  `IngestionService` importing process-management types, vector-store filter builders, *and*
  file-system APIs).
- Growing constructor/field count as a proxy — a class accumulating dependencies from several
  unrelated subsystems is often accumulating responsibilities too.
- This is a judgment call, not a hard rule — flag it as a discussion point ("this class is taking
  on N responsibilities, worth splitting?"), not a required fix, since splitting too early is its
  own cost.
- A diff that *adds* a new method to an existing class where the new method's concern is unrelated
  to the class's existing ones is the clearest trigger — e.g. adding image/thumbnail rendering or
  chat/alert notification logic (Slack, email) to a class whose existing methods are about
  ingestion, parsing, or data-store mutation. Don't wait for the class to already look bloated;
  flag the responsibility creep at the point the unrelated method is introduced.
