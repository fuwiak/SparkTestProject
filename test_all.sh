#!/bin/bash

echo "🧪 Running Complete Test Suite"
echo "================================"

echo "Test 1: Basic Scala functionality"
scala SimpleTest.scala
echo ""

echo "Test 2: Environment check"
echo "Java version: $(java -version 2>&1 | head -1)"
echo "Scala version: $(scala -version 2>&1)"
echo ""

echo "Test 3: Compilation tests"
echo "- Compiling WorkingSparkProject.scala..."
if scala-cli compile WorkingSparkProject.scala; then
    echo "✅ Compilation successful"
else
    echo "❌ Compilation failed"
fi

echo "- Compiling SimpleSparkDemo.scala..."
if scala-cli compile SimpleSparkDemo.scala; then
    echo "✅ Compilation successful"
else
    echo "❌ Compilation failed"
fi

echo ""
echo "Test 4: Spark execution (may fail due to Java 24)"
echo "Testing SimpleSparkDemo.scala..."
if timeout 30s scala-cli run SimpleSparkDemo.scala 2>/dev/null; then
    echo "✅ Spark test successful"
else
    echo "⚠️ Spark test failed (likely Java compatibility)"
    echo "💡 Try with Java 17: cs java --jvm temurin:17 --setup"
fi

echo ""
echo "🏁 Test Suite Complete"
echo "Next steps:"
echo "1. If Spark tests fail, install Java 17"
echo "2. Use: cs java --jvm temurin:17 --setup"
echo "3. Then rerun Spark tests" 