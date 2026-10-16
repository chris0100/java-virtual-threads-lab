package dev.chris0100.vthreads;

import java.time.Duration;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/** Limits concurrent calls to a downstream service when threads are no longer the limit. */
public final class RateLimitedClient {

    private final Semaphore permits;
    private final AtomicInteger inFlight = new AtomicInteger();
    private final AtomicInteger maxObserved = new AtomicInteger();

    public RateLimitedClient(int maxConcurrentCalls) {
        this.permits = new Semaphore(maxConcurrentCalls);
    }

    public String call(String request) throws InterruptedException {
        permits.acquire();
        try {
            int now = inFlight.incrementAndGet();
            maxObserved.accumulateAndGet(now, Math::max);
            BlockingWorkload.sleep(Duration.ofMillis(20));
            return "ok:" + request;
        } finally {
            inFlight.decrementAndGet();
            permits.release();
        }
    }

    public int maxObservedConcurrency() {
        return maxObserved.get();
    }
}
