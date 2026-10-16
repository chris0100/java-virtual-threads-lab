package dev.chris0100.vthreads;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/** Simulates many I/O-bound tasks (e.g. HTTP calls) and measures how long the batch takes. */
public final class BlockingWorkload {

    private BlockingWorkload() {
    }

    public static Duration run(ExecutorService executor, int tasks, Duration ioLatency, AtomicInteger completed) {
        long start = System.nanoTime();
        try (executor) {
            IntStream.range(0, tasks).forEach(i -> executor.submit(() -> {
                sleep(ioLatency);
                completed.incrementAndGet();
            }));
        } // close() waits for all tasks
        return Duration.ofNanos(System.nanoTime() - start);
    }

    public static ExecutorService virtual() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    public static ExecutorService platform(int size) {
        return Executors.newFixedThreadPool(size);
    }

    static void sleep(Duration d) {
        try {
            Thread.sleep(d);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        AtomicInteger done = new AtomicInteger();
        System.out.println("virtual : " + run(virtual(), 10_000, Duration.ofMillis(100), done).toMillis() + " ms");
        System.out.println("platform: " + run(platform(200), 10_000, Duration.ofMillis(100), done).toMillis() + " ms");
    }
}
