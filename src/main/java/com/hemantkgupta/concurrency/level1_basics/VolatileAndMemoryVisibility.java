package com.hemantkgupta.concurrency.level1_basics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Level 1: Volatile Keyword and Memory Visibility
 * 
 * INSIGHT: Volatile is critical for understanding memory models
 * - Guarantees visibility across threads (no caching in CPU registers)
 * - Prevents instruction reordering
 * - Does NOT provide atomicity (use Atomic* for that)
 * 
 * WHEN TO MENTION:
 * - Double-checked locking pattern (singleton)
 * - Flag-based thread coordination
 * - Publisher-subscriber patterns with single writer
 * 
 * PRODUCTION USAGE:
 * - Feature flags that change at runtime
 * - Circuit breaker states
 * - Configuration refresh flags
 */
public class VolatileAndMemoryVisibility {
    
    private static final Logger logger = LoggerFactory.getLogger(VolatileAndMemoryVisibility.class);
    
    /**
     * Example 1: WITHOUT volatile - May not see updates (race condition)
     * 
     * PROBLEM: CPU caching causes threads to not see updates from other threads
     * The JVM may cache this variable in CPU registers or L1/L2 cache
     */
    static class WithoutVolatile {
        private boolean running = true; // NO volatile
        private int counter = 0;
        
        public void start() {
            Thread writer = new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    logger.info("Writer: Setting running to false");
                    running = false; // Write
                    counter = 42;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Writer-Thread");
            
            Thread reader = new Thread(() -> {
                logger.info("Reader: Starting to read");
                int loopCount = 0;
                // WARNING: This might loop forever!
                while (running) {
                    loopCount++;
                    // Without volatile, this thread may never see running=false
                }
                logger.info("Reader: Exited loop after {} iterations, counter={}", 
                    loopCount, counter);
            }, "Reader-Thread");
            
            reader.start();
            writer.start();
            
            try {
                writer.join();
                reader.join(3000); // Wait max 3 seconds
                
                if (reader.isAlive()) {
                    logger.error("Reader thread is STILL RUNNING - visibility problem!");
                    reader.interrupt();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Example 2: WITH volatile - Guarantees visibility
     * 
     * SOLUTION: volatile ensures all threads see the latest value
     * - Happens-before relationship established
     * - No CPU caching, always reads from main memory
     */
    static class WithVolatile {
        private volatile boolean running = true; // WITH volatile
        private volatile int counter = 0;
        
        public void start() {
            Thread writer = new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    logger.info("Writer: Setting running to false");
                    running = false; // All threads will see this
                    counter = 42;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Writer-Thread");
            
            Thread reader = new Thread(() -> {
                logger.info("Reader: Starting to read");
                int loopCount = 0;
                while (running) {
                    loopCount++;
                }
                logger.info("Reader: Exited loop after {} iterations, counter={}", 
                    loopCount, counter);
            }, "Reader-Thread");
            
            reader.start();
            writer.start();
            
            try {
                writer.join();
                reader.join(3000);
                
                if (reader.isAlive()) {
                    logger.error("Unexpected: Reader still running even with volatile");
                } else {
                    logger.info("SUCCESS: Reader thread terminated correctly");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Example 3: Volatile does NOT guarantee atomicity
     * 
     * INSIGHT: Common interview mistake
     * - volatile only guarantees visibility
     * - increment operation is NOT atomic (read-modify-write)
     * - Use AtomicInteger for atomic operations
     */
    static class VolatileNonAtomicity {
        private volatile int counter = 0; // Visible but NOT atomic
        
        public void demonstrateRaceCondition() throws InterruptedException {
            Thread[] threads = new Thread[10];
            
            for (int i = 0; i < 10; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < 1000; j++) {
                        counter++; // NOT ATOMIC! (read, increment, write)
                    }
                });
                threads[i].start();
            }
            
            for (Thread thread : threads) {
                thread.join();
            }
            
            logger.info("Expected: 10000, Actual: {} (race condition!)", counter);
            // Result will be less than 10000 due to lost updates
        }
    }
    
    /**
     * Example 4: Double-Checked Locking Pattern (Correct)
     * 
     * INSIGHT: Classic interview question
     * - volatile is REQUIRED here to prevent instruction reordering
     * - Without volatile, partially constructed object can be visible
     * 
     * WHEN TO MENTION: Singleton pattern, lazy initialization
     */
    static class Singleton {
        // Must be volatile to prevent instruction reordering
        private static volatile Singleton instance;
        private final String data;
        
        private Singleton() {
            // Simulate expensive initialization
            this.data = "Initialized at " + System.currentTimeMillis();
        }
        
        public static Singleton getInstance() {
            if (instance == null) { // First check (no locking)
                synchronized (Singleton.class) {
                    if (instance == null) { // Second check (with lock)
                        instance = new Singleton();
                    }
                }
            }
            return instance;
        }
        
        public String getData() {
            return data;
        }
    }
    
    /**
     * Example 5: Volatile for coordinating threads
     * 
     * PRODUCTION PATTERN: Graceful shutdown mechanism
     */
    static class GracefulShutdown {
        private volatile boolean shutdownRequested = false;
        
        public void startWorker() {
            Thread worker = new Thread(() -> {
                logger.info("Worker started");
                int taskCount = 0;
                
                while (!shutdownRequested) {
                    // Simulate work
                    taskCount++;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                
                logger.info("Worker shutting down gracefully after {} tasks", taskCount);
            }, "Worker");
            
            worker.start();
            
            // Simulate runtime
            try {
                Thread.sleep(1000);
                logger.info("Requesting shutdown");
                shutdownRequested = true; // Worker will see this
                worker.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        logger.info("=== Demonstrating Memory Visibility Issues ===\n");
        
        logger.info("1. WITHOUT volatile (may hang):");
        // Uncomment to see the problem (it might hang!)
        // new WithoutVolatile().start();
        
        logger.info("\n2. WITH volatile (works correctly):");
        new WithVolatile().start();
        
        logger.info("\n3. Volatile does NOT provide atomicity:");
        new VolatileNonAtomicity().demonstrateRaceCondition();
        
        logger.info("\n4. Double-Checked Locking:");
        Singleton s1 = Singleton.getInstance();
        Singleton s2 = Singleton.getInstance();
        logger.info("Same instance? {}", s1 == s2);
        logger.info("Data: {}", s1.getData());
        
        logger.info("\n5. Graceful Shutdown Pattern:");
        new GracefulShutdown().startWorker();
    }
}
