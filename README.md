# Java Virtual Threads Lab (Java 21)

Experiments to understand when virtual threads help and when they don't.

| Example | Class | Lesson |
|---------|-------|--------|
| 10,000 blocking tasks | `BlockingWorkload` | Virtual threads finish I/O-bound work much faster than a small platform pool, without tuning |
| Limiting concurrency | `RateLimitedClient` | With virtual threads you limit with a `Semaphore`, not with the pool size |
| Pinning | `PinningExample` | `synchronized` around blocking code pins the carrier thread; `ReentrantLock` does not (Java 21) |

## Run

```bash
mvn test
# optional: see pinning events (run the main class from your IDE with this VM option)
#   -Djdk.tracePinnedThreads=full   ->  dev.chris0100.vthreads.PinningExample
```

## Interview notes

- Virtual threads are cheap (KBs, not MBs) and are scheduled by the JVM on a small pool of carrier threads.
- They help with **blocking I/O** (HTTP calls, JDBC, queues). They do **not** make CPU-bound work faster.
- Don't pool virtual threads. Create one per task: `Executors.newVirtualThreadPerTaskExecutor()`.
- Protect scarce resources (DB connections, rate-limited APIs) with a `Semaphore` or the connection pool itself.
- In Java 21, `synchronized` + blocking pins the carrier. Prefer `ReentrantLock` in hot paths (fixed in Java 24, JEP 491).
- `ThreadLocal` still works, but with millions of threads it can use a lot of memory. Look at Scoped Values (preview).
- Spring Boot 3.2+: `spring.threads.virtual.enabled=true`.
