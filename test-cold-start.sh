#!/bin/bash

# Build the project
echo "Building project..."
./mvnw clean package -DskipTests

# Create test event
cat > test-event.json << EOF
{
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
}
EOF

# Run cold start test
echo "Running cold start test..."
start_time=$(date +%s.%N)
java -jar target/template-service-1.0.0-aws.jar --spring.profiles.active=lambda < test-event.json
end_time=$(date +%s.%N)
execution_time=$(echo "$end_time - $start_time" | bc)
echo "Cold start execution time: $execution_time seconds" 