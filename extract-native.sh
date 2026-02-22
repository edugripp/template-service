#!/bin/bash

# Create a temporary container from the image
CONTAINER_ID=$(docker create --network template-service_default egripp/spring3-native-template/template-service:1.0.0)

# Create target directory if it doesn't exist
mkdir -p target

# Copy the native executable from the container
docker cp $CONTAINER_ID:/app/template-service target/

# Remove the temporary container
docker rm $CONTAINER_ID

echo "Native executable extracted to target/template-service" 