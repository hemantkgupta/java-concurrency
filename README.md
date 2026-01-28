# Java Concurrency Mastery

A comprehensive hands-on project to master Java concurrency from beginner to advanced level for Meta E7 System Design Interviews.

## Learning Path

### **Level 1: Basics** (Beginner)
- Thread creation and lifecycle
- Runnable vs Callable
- Thread sleep, join, interrupt
- Volatile keyword
- Memory visibility issues

### **Level 2: Synchronization** (Beginner-Intermediate)
- synchronized keyword (method & block)
- ReentrantLock
- ReadWriteLock
- Lock fairness
- Deadlock scenarios

### **Level 3: Coordination** (Intermediate)
- CountDownLatch
- CyclicBarrier
- Semaphore
- Phaser
- Exchanger

### **Level 4: Concurrent Collections** (Intermediate)
- ConcurrentHashMap
- CopyOnWriteArrayList
- BlockingQueue variants
- ConcurrentSkipListMap
- LinkedTransferQueue

### **Level 5: Atomic Variables** (Intermediate-Advanced)
- AtomicInteger/AtomicLong
- AtomicReference
- LongAdder/DoubleAdder
- AtomicStampedReference (ABA problem)
- Lock-free algorithms

### **Level 6: Executors & Thread Pools** (Advanced)
- ThreadPoolExecutor configuration
- ScheduledExecutorService
- ForkJoinPool
- CompletableFuture
- Custom rejection policies

### **Level 7: Advanced Patterns** (Advanced)
- Producer-Consumer patterns
- Rate limiters (Token Bucket, Leaky Bucket)
- Object pools
- Custom synchronizers with AQS
- Thread-safe data structures

## Setup

### Prerequisites
- Java 17+
- Maven 3.8+

### Build
```bash
mvn clean install
```

### Run Tests
```bash
# Run all tests
mvn test

# Run specific level tests
mvn test -Dtest="*Level1*"
mvn test -Dtest="*Level2*"
```

### Run Examples
```bash
# Compile first
mvn compile

# Run specific example
mvn exec:java -Dexec.mainClass="com.meta.concurrency.level1_basics.BasicThreadExample"
```

## 📖 How to Use This Project

1. **Sequential Learning**: Start from Level 1 and progress through each level
2. **Read Code**: Each class has extensive comments explaining concepts
3. **Run Tests**: Tests demonstrate both correct usage and common pitfalls
4. **Experiment**: Modify code to see different behaviors
5. **Interview Focus**: Comments highlight Meta E7-level insights

## Tips

Each level includes:
- **Production-ready patterns** used at Meta scale
- **Common pitfalls** to avoid in interviews
- **E7-level insights** on performance and trade-offs
- **When to use** each primitive in system design

## Project Structure

```
src/main/java/com/meta/concurrency/
├── level1_basics/           # Thread fundamentals
├── level2_synchronization/  # Locks and synchronized
├── level3_coordination/     # CountDownLatch, Semaphore, etc.
├── level4_collections/      # Concurrent collections
├── level5_atomics/          # Atomic variables
├── level6_executors/        # Thread pools and CompletableFuture
└── level7_advanced/         # Advanced patterns

src/test/java/com/meta/concurrency/
└── [same structure with tests]
```

## Testing Strategy

- **Unit Tests**: Verify correctness of concurrent code
- **Stress Tests**: Test under high contention
- **Race Condition Tests**: Demonstrate thread safety issues
- **Performance Tests**: Compare different approaches

## Notes

- All examples use Java 17 features
- Tests use JUnit 5, AssertJ, and Awaitility
- Logging configured with SLF4J + Logback
- Examples focus on practical system design scenarios

## Next Steps After Completion

1. Build mini-projects: Rate Limiter, Cache, Job Scheduler
2. Practice system design with concurrency focus
3. Review Meta's production systems (Threads, Instagram feed processing)
4. Study distributed concurrency (Redis locks, ZooKeeper)

---

**Author**: Meta E7 Interview Preparation  
**Last Updated**: January 2026
