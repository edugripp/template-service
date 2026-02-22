#!/bin/bash

# Build native image
if [ ! -f target/template-service ]; then
  echo "Building native image..."
  ./mvnw clean package -Pnative -DskipTests
fi

# Download RIE if not present
if [ ! -f aws-lambda-rie ]; then
    echo "Downloading AWS Lambda Runtime Interface Emulator..."
    curl -Lo aws-lambda-rie https://github.com/aws/aws-lambda-runtime-interface-emulator/releases/latest/download/aws-lambda-rie
    chmod +x aws-lambda-rie
fi

# Function to measure execution time of a single request
measure_execution() {
    local request_num=$1
    echo "Running request $request_num..."
    start_time=$(date +%s.%N)
    
    # Simular uma invocação Lambda
    curl -XPOST "http://localhost:8080/2015-03-31/functions/function/invocations" -d '{
        "httpMethod": "GET",
        "path": "/template",
        "queryStringParameters": {
            "page": "1",
            "pageSize": "10",
            "sort": "asc",
            "sortField": "id"
        },
        "headers": {
            "accept": "*/*",
            "Authorization": "Basic YWRtaW46YWRtaW4="
        }
    }'
    
    end_time=$(date +%s.%N)
    execution_time=$(echo "$end_time - $start_time" | bc)
    echo "Request $request_num execution time: $execution_time seconds"
    echo "----------------------------------------"
}

# Cold Start Test
echo "Starting Cold Start Test (Native Lambda)..."
start_time=$(date +%s.%N)

# Iniciar o RIE com o binário nativo
SPRING_PROFILES_ACTIVE=local ./aws-lambda-rie target/template-service > app.log 2>&1 &
APP_PID=$!

# Wait for the application to start
echo "Waiting for Lambda to start..."
while ! grep -q "Started" app.log; do
    sleep 1
done

# First request (cold start)
echo "First request (cold start):"
measure_execution 1

end_time=$(date +%s.%N)
cold_start_time=$(echo "$end_time - $start_time" | bc)
echo "Total Cold Start Time (including startup): $cold_start_time seconds"
echo "----------------------------------------"

# Wait a bit
sleep 2

# Second request (warm start)
echo "Second request (warm start):"
measure_execution 2

# Wait a bit
sleep 2

# Third request (warm start)
echo "Third request (warm start):"
measure_execution 3

# Cleanup
echo "Stopping Lambda..."
kill $APP_PID
rm app.log 