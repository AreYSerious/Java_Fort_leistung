#!/bin/bash

# Build script for Social Media Platform

echo "Building Social Media Platform..."

# Clean previous build
rm -rf build/classes
mkdir -p build/classes

# Compile main source files
echo "Compiling main sources..."
javac -cp "lib/*" -d build/classes $(find src/main/java -name "*.java")
if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

# Copy resources
echo "Copying resources..."
cp src/main/resources/* build/classes/

# Compile test files
echo "Compiling tests..."
javac -cp "lib/*:build/classes" -d build/classes $(find src/test/java -name "*.java")
if [ $? -ne 0 ]; then
    echo "Test compilation failed!"
    exit 1
fi

echo "Build successful!"
