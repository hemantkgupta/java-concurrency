#!/bin/bash

# Java Concurrency Mastery - Build Script
# Since Maven may not be available, using javac directly

PROJECT_ROOT="/home/claude/java-concurrency-mastery"
SRC_DIR="$PROJECT_ROOT/src/main/java"
TARGET_DIR="$PROJECT_ROOT/target/classes"

# Create output directory
mkdir -p "$TARGET_DIR"

echo "=== Compiling Java Concurrency Examples ==="

# Compile Level 1 Basics
echo "Compiling Level 1: Basics..."
javac -d "$TARGET_DIR" \
    "$SRC_DIR/com/meta/concurrency/level1_basics/ThreadCreationBasics.java" \
    "$SRC_DIR/com/meta/concurrency/level1_basics/VolatileAndMemoryVisibility.java"

if [ $? -eq 0 ]; then
    echo "✓ Compilation successful!"
    echo ""
    echo "=== Running Examples ==="
    echo ""
    
    echo "1. Thread Creation Basics:"
    java -cp "$TARGET_DIR" com.meta.concurrency.level1_basics.ThreadCreationBasics
    
    echo ""
    echo "2. Volatile and Memory Visibility:"
    java -cp "$TARGET_DIR" com.meta.concurrency.level1_basics.VolatileAndMemoryVisibility
    
else
    echo "✗ Compilation failed"
    exit 1
fi
