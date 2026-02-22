#!/bin/bash

# Download RIE if not present
if [ ! -f aws-lambda-rie ]; then
    echo "Downloading AWS Lambda Runtime Interface Emulator..."
    curl -Lo aws-lambda-rie https://github.com/aws/aws-lambda-runtime-interface-emulator/releases/latest/download/aws-lambda-rie
    chmod +x aws-lambda-rie
fi

# Create test event
cat > test-event.json << EOF
{
    "httpMethod": "GET",
    "path": "/template",
    "queryStringParameters": {
        "page": "0",
        "size": "10"
    },
    "headers": {
        "Content-Type": "application/json"
    }
}
EOF

# Execute the Native Lambda
echo "Executing Native Lambda..."
time ./target/template-service --spring.profiles.active=lambda < test-event.json

# Cleanup
rm test-event.json 