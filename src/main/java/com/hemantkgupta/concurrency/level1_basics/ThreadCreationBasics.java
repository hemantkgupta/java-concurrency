package com.hemantkgupta.concurrency.level1_basics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Level 1: Thread Creation Basics
 * 
 * Understanding thread creation is fundamental, but in production:
 * - Never create threads directly (use ExecutorService)
 * - Thread creation is expensive (~1MB stack per thread)
 * - Context switching overhead becomes significant with too many threads
 */
public class ThreadCreationBasics {
    
    private static final Logger logger = LoggerFactory.getLogger(ThreadCreationBasics.class);
    
    /**
     * Method 1: Extending Thread class
     * 
     * PROS: Simple, straightforward
     * CONS: Can't extend another class (Java single inheritance)
     * 
     * TIP: This is not preferred in production
     */
    static class WorkerThread extends Thread {
        private final String taskName;
        
        public WorkerThread(String taskName) {
            this.taskName = taskName;
            setName("Worker-" + taskName);
        }
        
        @Override
        public void run() {
            logger.info("Thread {} starting task: {}", 
                Thread.currentThread().getName(), taskName);
            
            try {
                Thread.sleep(1000);
                logger.info("Thread {} completed task: {}", 
                    Thread.currentThread().getName(), taskName);
            } catch (InterruptedException e) {
                // IMPORTANT: Preserve interrupt status
                Thread.currentThread().interrupt();

                logger.error("Thread {} was interrupted", 
                    Thread.currentThread().getName());
            }
        }
    }
    
    /**
     * Method 2: Implementing Runnable interface (PREFERRED)
     * 
     * PROS: Can implement other interfaces, better separation of concerns
     * CONS: None really
     * 
     * INSIGHT: This is the standard approach. Separates task from execution.
     */
    static class WorkerTask implements Runnable {
        private final String taskName;
        
        public WorkerTask(String taskName) {
            this.taskName = taskName;
        }
        
        @Override
        public void run() {
            logger.info("Runnable task {} executing on thread {}", 
                taskName, Thread.currentThread().getName());
            
            try {
                Thread.sleep(1000);
                logger.info("Runnable task {} completed", taskName);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Runnable task {} interrupted", taskName);
            }
        }
    }
    
    /**
     * Method 3: Lambda expression (Java 8+)
     * 
     * INSIGHT: Most concise for simple tasks.
     */
    public static void demonstrateLambdaThread() {
        Thread thread = new Thread(() -> {
            logger.info("Lambda thread executing on {}", 
                Thread.currentThread().getName());
        });
        thread.start();
    }
    
    /**
     * Demonstrates thread lifecycle states
     * 
     * Thread States: NEW -> RUNNABLE -> (BLOCKED/WAITING/TIMED_WAITING) -> TERMINATED
     * 
     * TIP: Mention monitoring thread states in production (thread dumps)
     */
    public static void demonstrateThreadStates() throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        logger.info("Thread state after creation: {}", thread.getState()); // NEW
        
        thread.start();
        logger.info("Thread state after start: {}", thread.getState()); // RUNNABLE
        
        Thread.sleep(100); // Let it start sleeping
        logger.info("Thread state while sleeping: {}", thread.getState()); // TIMED_WAITING
        
        thread.join(); // Wait for completion
        logger.info("Thread state after completion: {}", thread.getState()); // TERMINATED
    }
    
    public static void main(String[] args) throws InterruptedException {
        logger.info("=== Thread Creation Examples ===");
        
        // Example 1: Extending Thread
        WorkerThread t1 = new WorkerThread("Task-1");
        t1.start(); // IMPORTANT: Call start(), not run()
        
        // Example 2: Implementing Runnable
        Thread t2 = new Thread(new WorkerTask("Task-2"));
        t2.start();
        
        // Example 3: Lambda
        demonstrateLambdaThread();
        
        // Wait for all threads to complete
        t1.join();
        t2.join();
        
        logger.info("\n=== Thread State Examples ===");
        demonstrateThreadStates();
        
        logger.info("\nAll examples completed");
    }
}
