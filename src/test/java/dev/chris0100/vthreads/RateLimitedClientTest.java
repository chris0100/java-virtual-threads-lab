package dev.chris0100.vthreads;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitedClientTest {

    @Test
    void semaphoreCapsConcurrencyEvenWithThousandsOfVirtualThreads() {
        RateLimitedClient client = new RateLimitedClient(10);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 1_000).forEach(i -> executor.submit(() -> client.call("req-" + i)));
        }

        int max = client.maxObservedConcurrency();
        assertTrue(max > 0 && max <= 10, "max concurrency was " + max);
    }
}
