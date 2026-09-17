---
id: anti-pattern.third-party-or-long-io-in-transactional
category: performance
severity: high
applies_to: "@Transactional (Spring Boot, imperative/JDBC transactions)"
---

# Calling a third-party service or doing long I/O inside @Transactional

## Why it matters

Classic (non-reactive) `@Transactional` checks out a database connection from the pool for the
entire duration of the annotated method and holds it — along with any row/table locks the
transaction has already acquired — until the method returns and the surrounding proxy commits or
rolls back. Any slow work placed inside that method (an HTTP call to a third-party API, a Kafka
publish, an LDAP lookup, file I/O) extends how long that connection and those locks are held, for
a reason that has nothing to do with the database. Under load this is exactly how connection-pool
exhaustion starts: N concurrent requests each waiting on the same slow third party are each pinning
a DB connection they aren't actively using.

Beyond hold time, firing an external side effect from inside a transaction *before it commits* is a
correctness problem, not just a performance one: the outside world sees the event before the write
is guaranteed durable. If the method throws right after the external call, or the commit itself
fails, a consumer downstream has now reacted to something that was never actually persisted.

## Real instance in this codebase

```java
// auth-service/src/main/java/com/takypok/authservice/service/impl/UserServiceImpl.java
@Override
@Transactional
public void create(CreateUserRequest request) {
  ...
  userDetailsManager.createUser(user);
  ...
  userinfoRepository.save(userinfo);

  publishAccountEvent(userinfo); // Kafka publish — still inside the open transaction
}

private void publishAccountEvent(Userinfo userinfo) {
  ...
  kafkaTemplate.send(accountEventsTopic, event.getSub(), event)...
}
```

`create` (and `updateProfile` in the same file) publish to Kafka as the last step of a
`@Transactional` method. The DB connection backing the transaction stays checked out for the round
trip to the Kafka broker. Worse: if the transaction fails to commit *after* `publishAccountEvent`
returns (a deferred constraint check, a dropped connection during commit — rare, but possible), a
consumer has already received an `AccountEvent` for a `Userinfo` row that doesn't exist yet.

## Good example

Publish only after the transaction has actually committed, using Spring's transaction-synchronization
hook, so the event fires once the write is durable and a slow/failing broker can no longer extend or
break the DB transaction itself:

```java
@Override
@Transactional
public void create(CreateUserRequest request) {
  ...
  userinfoRepository.save(userinfo);
  eventPublisher.publishEvent(new UserinfoCreated(userinfo)); // in-JVM, no I/O
}

@Component
@RequiredArgsConstructor
class AccountEventRelay {
  private final KafkaTemplate<String, AccountEvent> kafkaTemplate;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  void onUserinfoCreated(UserinfoCreated event) {
    kafkaTemplate.send(...); // runs after commit, outside the transactional method entirely
  }
}
```

This also decouples the two failure domains: a slow or failing Kafka broker can no longer cause the
user-creation transaction to run long or roll back.

## Detection heuristic

- A call to a messaging client (`KafkaTemplate`, `RabbitTemplate`, `JmsTemplate`), an HTTP client
  (`WebClient`, `RestTemplate`, a `@FeignClient`), an LDAP/SMTP client, or any third-party SDK,
  appearing textually inside a method annotated `@Transactional` — including inside a private
  helper that method calls.
- Especially watch for that call being the *last* statement in the method — a common shape ("do the
  DB writes, then notify") that's easy to miss because it reads as harmless.
- A `@Transactional` method whose body does file I/O, large in-memory report/document generation,
  or any work whose duration scales with something other than the DB query itself.

## Related but distinct

`workflow-service/.../TicketExportService.export` is `@Transactional(readOnly = true)` on a method
returning `Mono<byte[]>` — a different problem worth its own rule later: classic `@Transactional`
binds to the calling thread and commits when the method *returns*, which happens before the
returned `Mono`'s pipeline (including the actual `stream()` repository calls, dispatched onto
`boundedElastic`) ever runs. The `readOnly` transaction boundary here likely doesn't cover the work
it looks like it covers — that's a "`@Transactional` + reactive return type" mismatch, not a
third-party-call-in-transaction issue, so it isn't folded into this doc.
