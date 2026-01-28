package com.hemantkgupta.concurrency.level1_basics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

/**
 * Tests for Thread Creation Basics
 * 
 * TESTING STRATEGY FOR CONCURRENT CODE:
 * 1. Use Awaitility for async assertions
 * 2. Use AtomicInteger for thread-safe counters
 * 3. Always set test timeouts to prevent hanging
 * 4. Test both success and failure scenarios
 */
class ThreadCreationBasicsTest {
    
    @Test
    @DisplayName("Thread created by extending Thread class should complete execution")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testWorkerThreadExecution() throws InterruptedException {
        // Given
        AtomicInteger taskCompleted = new AtomicInteger(0);
        
        ThreadCreationBasics.WorkerThread thread = new ThreadCreationBasics.WorkerThread("Test-Task") {
            @Override
            public void run() {
                super.run();
                taskCompleted.incrementAndGet();
            }
        };
        
        // When
        thread.start();
        thread.join();
        
        // Then
        assertThat(thread.getState()).isEqualTo(Thread.State.TERMINATED);
        assertThat(taskCompleted.get()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Thread created with Runnable should complete execution")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testRunnableExecution() throws InterruptedException {
        // Given
        AtomicInteger counter = new AtomicInteger(0);
        Runnable task = () -> {
            counter.incrementAndGet();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        
        // When
        Thread thread = new Thread(task);
        thread.start();
        thread.join();
        
        // Then
        assertThat(counter.get()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Multiple threads should execute concurrently")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testConcurrentExecution() throws InterruptedException {
        // Given
        AtomicInteger counter = new AtomicInteger(0);
        int threadCount = 5;
        Thread[] threads = new Thread[threadCount];
        
        // When
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                counter.incrementAndGet();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            threads[i].start();
        }
        
        // Wait for all threads
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Then
        assertThat(counter.get()).isEqualTo(threadCount);
    }
    
    @Test
    @DisplayName("Thread states should transition correctly")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testThreadStates() throws InterruptedException {
        // Given
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Then - NEW state
        assertThat(thread.getState()).isEqualTo(Thread.State.NEW);
        
        // When - Start thread
        thread.start();
        
        // Then - RUNNABLE or TIMED_WAITING
        Thread.sleep(100); // Give it time to start sleeping
        assertThat(thread.getState())
            .isIn(Thread.State.RUNNABLE, Thread.State.TIMED_WAITING);
        
        // Wait for completion
        thread.join();
        
        // Then - TERMINATED
        assertThat(thread.getState()).isEqualTo(Thread.State.TERMINATED);
    }
    
    @Test
    @DisplayName("Thread interrupt should set interrupted flag")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testThreadInterrupt() throws InterruptedException {
        // Given
        AtomicInteger interruptedCount = new AtomicInteger(0);
        
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(5000); // Long sleep
            } catch (InterruptedException e) {
                interruptedCount.incrementAndGet();
                Thread.currentThread().interrupt(); // Preserve status
            }
        });
        
        // When
        thread.start();
        Thread.sleep(100); // Let thread start sleeping
        thread.interrupt(); // Interrupt it
        thread.join();
        
        // Then
        assertThat(interruptedCount.get()).isEqualTo(1);
        assertThat(thread.isInterrupted()).isTrue();
    }
    
    @Test
    @DisplayName("Calling run() directly should NOT create new thread")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testRunVsStart() {
        // Given
        String mainThreadName = Thread.currentThread().getName();
        AtomicInteger sameThreadFlag = new AtomicInteger(0);
        
        Runnable task = () -> {
            if (Thread.currentThread().getName().equals(mainThreadName)) {
                sameThreadFlag.set(1); // Same thread
            } else {
                sameThreadFlag.set(2); // Different thread
            }
        };
        
        // When - Call run() directly (WRONG)
        Thread thread = new Thread(task);
        thread.run(); // This executes on current thread
        
        // Then - Executes on same thread
        assertThat(sameThreadFlag.get()).isEqualTo(1);
        assertThat(thread.getState()).isEqualTo(Thread.State.NEW); // Never started!
    }
    
    @Test
    @DisplayName("Using Awaitility for async assertions")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testAwaitilityPattern() {
        // Given
        AtomicInteger counter = new AtomicInteger(0);
        
        // When - Start async task
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                counter.set(42);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        
        // Then - Wait for async completion
        await()
            .atMost(3, TimeUnit.SECONDS)
            .untilAsserted(() -> assertThat(counter.get()).isEqualTo(42));
    }
}
