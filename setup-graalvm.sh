#!/bin/bash

echo "Starting JDK 25 Native Image environment setup..."

if ! command -v sdk &> /dev/null
then
    echo "SDKMAN not found. Installing SDKMAN first..."
    curl -s "https://get.sdkman.io" | bash
    source "$HOME/.sdkman/bin/sdkman-init.sh"
fi

echo "Installing OpenJDK 25..."
sdk install java 25-open
sdk use java 25-open

# In recent Java versions, GraalVM native-image runs out-of-the-box or is bundled differently.
# `gu install` is deprecated or removed in modern Oracle GraalVM distributions. 

echo "Java 25 setup complete!"
echo "If you use a Mac M1/M2/M3 (ARM64), remember that the Maven build is pre-configured to build the Container Image natively:"
echo "./mvnw spring-boot:build-image -Pnative -DskipTests"
echo ""
echo "Please restart your terminal or run this command in your current shell:"
echo "source ~/.sdkman/bin/sdkman-init.sh && sdk use java 25-open"