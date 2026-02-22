#!/bin/bash

# Create test event
cat > test-event.json << EOF
{
  "httpMethod": "GET",
  "path": "/template",
  "headers": {
    "Content-Type": "application/json"
  }
}
EOF

# First, extract the native executable from the Docker image
./extract-native.sh

echo "Testing Lambda function..."

# Record start time
start_time=$(date +%s.%N)

# Execute Lambda function with local profile
./target/template-service -Dspring.profiles.active=local < test-event.json

# Record end time and calculate duration
end_time=$(date +%s.%N)
duration=$(echo "$end_time - $start_time" | bc)

echo "Execution time: $duration seconds"

# Cleanup
rm test-event.json 