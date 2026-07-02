#!/bin/bash

# Run tests for the Social Media Platform

echo "Running tests..."
java -cp "build/classes:lib/*" -ea com.socialmedia.TestRunner
