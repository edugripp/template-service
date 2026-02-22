#!/bin/bash

# Build the project
echo "Building project..."
./mvnw clean package -DskipTests

# Function to run the Lambda and measure time
run_lambda() {
    echo "Running Lambda invocation $1..."
    start_time=$(date +%s.%N)
    ./mvnw spring-boot:run -Dspring-boot.run.profiles=local -Dspring-boot.run.jvmArguments="-Dserver.port=0"
    end_time=$(date +%s.%N)
    execution_time=$(echo "$end_time - $start_time" | bc)
    echo "Execution time: $execution_time seconds"
    echo "----------------------------------------"
}

# Run first invocation (cold start)
echo "First invocation (cold start):"
run_lambda 1

# Wait a bit to ensure the environment is still warm
sleep 2

# Run second invocation (warm start)
echo "Second invocation (warm start):"
run_lambda 2

# Wait a bit more
sleep 2

# Run third invocation (warm start)
echo "Third invocation (warm start):"
run_lambda 3 