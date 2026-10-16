package dev.chris0100.vthreads;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class BlockingWorkloadTest {

    @Test
    void virtualThreadsHandleManyBlockingTasksQuickly() {
        AtomicInteger virtualDone = new AtomicInteger();
        AtomicInteger platformDone = new AtomicInteger();

        Duration virtual = BlockingWorkload.run(BlockingWorkload.virtual(), 5_000, Duration.ofMillis(50), virtualDone);
        Duration platform = BlockingWorkload.run(BlockingWorkload.platform(50), 5_000, Duration.ofMillis(50), platformDone);

        assertEquals(5_000, virtualDone.get());
        assertEquals(5_000, platformDone.get());
        // 5,000 tasks / 50 threads * 50 ms = ~5 s for the platform pool; virtual threads run them almost all at once
        assertTrue(virtual.compareTo(platform) < 0, "virtual=" + virtual + " platform=" + platform);
    }
}
