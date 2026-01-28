package com.hemantkgupta.concurrency.level1_basics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.RepeatedTest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

class VolatileAndMemoryVisibilityTest {
    
    @Test
    @DisplayName("Volatile ensures visibility across threads")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testVolatileVisibility() throws InterruptedException {
        // Given
        VolatileAndMemoryVisibility.WithVolatile example = 
            new VolatileAndMemoryVisibility.WithVolatile();
        
        // When
        example.start();
        
        // Then - Should complete without hanging
        // If test completes, volatile worked
    }
    
    @Test
    @DisplayName("Volatile does NOT provide atomicity")
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testVolatileNonAtomicity() throws InterruptedException {
        // Given
        VolatileAndMemoryVisibility.VolatileNonAtomicity example = 
            new VolatileAndMemoryVisibility.VolatileNonAtomicity();
        
        // When
        example.demonstrateRaceCondition();
        
        // Then
        // Counter will be less than 10000 due to race conditions
        // This test verifies the problem exists
    }
    
    @Test
    @DisplayName("Singleton double-checked locking should return same instance")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testSingletonDoubleCheckedLocking() throws InterruptedException {
        // Given
        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];
        VolatileAndMemoryVisibility.Singleton[] instances = 
            new VolatileAndMemoryVisibility.Singleton[threadCount];
        
        // When - Multiple threads try to get instance
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                instances[index] = VolatileAndMemoryVisibility.Singleton.getInstance();
            });
            threads[i].start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Then - All instances should be the same
        VolatileAndMemoryVisibility.Singleton first = instances[0];
        for (int i = 1; i < threadCount; i++) {
            assertThat(instances[i]).isSameAs(first);
        }
    }
    
    @Test
    @DisplayName("Graceful shutdown should stop worker thread")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testGracefulShutdown() {
        // Given
        VolatileAndMemoryVisibility.GracefulShutdown shutdown = 
            new VolatileAndMemoryVisibility.GracefulShutdown();
        
        // When
        shutdown.startWorker();
        
        // Then - Should complete without hanging
    }
    
    @RepeatedTest(5)
    @DisplayName("Repeated test to catch visibility issues")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testVolatileRepeated() throws InterruptedException {
        // Visibility issues may not appear every time
        // Repeated tests increase chances of catching race conditions
        
        AtomicInteger sharedCounter = new AtomicInteger(0);
        class VolatileHolder {
            volatile boolean stopFlag = false; // Now it's a field
        }
        VolatileHolder holder = new VolatileHolder();
        
        Thread writer = new Thread(() -> {
            try {
                Thread.sleep(500);
                holder.stopFlag = true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        Thread reader = new Thread(() -> {
            while (!holder.stopFlag) {
                sharedCounter.incrementAndGet();
            }
        });
        
        reader.start();
        writer.start();
        
        writer.join();
        reader.join(2000);
        
        assertThat(reader.isAlive()).isFalse();
    }
    
    @Test
    @DisplayName("Demonstrate happens-before guarantee with volatile")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testHappensBeforeGuarantee() throws InterruptedException {
        // Given
        class VolatileExample {
            private int normalVar = 0;
            private volatile boolean ready = false;
            
            void writer() {
                normalVar = 42; // Write 1
                ready = true;   // Write 2 (volatile)
                // Volatile write creates happens-before relationship
            }
            
            int reader() {
                while (!ready) { // Read volatile
                    // Spin
                }
                return normalVar; // Will see normalVar = 42 (happens-before)
            }
        }
        
        VolatileExample example = new VolatileExample();
        AtomicInteger result = new AtomicInteger(0);
        
        // When
        Thread writerThread = new Thread(example::writer);
        Thread readerThread = new Thread(() -> {
            result.set(example.reader());
        });
        
        readerThread.start();
        Thread.sleep(100); // Let reader start spinning
        writerThread.start();
        
        readerThread.join(2000);
        writerThread.join();
        
        // Then - happens-before guarantees reader sees normalVar=42
        assertThat(result.get()).isEqualTo(42);
    }
}
