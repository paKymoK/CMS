---
id: anti-pattern.n-plus-one-query
category: performance
severity: high
applies_to: any ORM/repository usage inside a loop or collection-processing chain
---

# One query per item instead of one batched query

## Why it matters

Fetching a list of parent records and then querying for each one's related data inside a loop
issues N+1 round trips to the database instead of one or two. It works correctly and looks
harmless with a handful of test rows, then degrades linearly (or worse, with nested cases)
as the dataset grows — a page that's fast in dev with 10 rows can mean hundreds of queries and
seconds of latency in production with 500. This is one of the most common performance regressions
because it's invisible in code review unless you're specifically looking for a query call sitting
inside iteration.

## Bad example

```java
List<Order> orders = orderRepository.findByCustomerId(customerId);
for (Order order : orders) {
  order.setItems(orderItemRepository.findByOrderId(order.getId())); // one query per order
}
```

## Good example

```java
List<Order> orders = orderRepository.findByCustomerId(customerId);
List<Long> orderIds = orders.stream().map(Order::getId).toList();
Map<Long, List<OrderItem>> itemsByOrder = orderItemRepository.findByOrderIdIn(orderIds)
    .stream().collect(Collectors.groupingBy(OrderItem::getOrderId)); // one query total
orders.forEach(o -> o.setItems(itemsByOrder.getOrDefault(o.getId(), List.of())));
```

## Detection heuristic

- A repository/JPA/R2DBC call inside a `for` loop, `forEach`, or `.map()`/`.flatMap()` over a
  collection that was itself just fetched from the database.
- Lazy-loaded association access (`order.getItems()`) inside a loop over the owning entities,
  where the association isn't eagerly fetched or batch-loaded.
- Look for a `findByXIn(List<...>)`/`IN (...)` query already available on the same repository —
  if one exists and the loop isn't using it, that's the fix sitting right there unused.
