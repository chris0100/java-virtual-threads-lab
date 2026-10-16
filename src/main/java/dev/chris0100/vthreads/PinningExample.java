package dev.chris0100.vthreads;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Run with -Djdk.tracePinnedThreads=full to see the JVM report pinning for the synchronized version.
 */
public final class PinningExample {

    private static final Object MONITOR = new Object();
    private static final ReentrantLock LOCK = new ReentrantLock();

    static void withSynchronized() {
        synchronized (MONITOR) {
            BlockingWorkload.sleep(Duration.ofMillis(10)); // pins the carrier thread in Java 21
        }
    }

    static void withReentrantLock() {
        LOCK.lock();
        try {
            BlockingWorkload.sleep(Duration.ofMillis(10)); // virtual thread can unmount
        } finally {
            LOCK.unlock();
        }
    }

    public static void main(String[] args) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 5; i++) {
                executor.submit(PinningExample::withSynchronized);
                executor.submit(PinningExample::withReentrantLock);
            }
        }
        System.out.println("done - check the console for pinned thread stack traces");
    }
}
