#!/bin/bash

# Set GraalVM home
GRAALVM_HOME="/opt/homebrew/Caskroom/graalvm-jdk21/21.0.7/graalvm-jdk-21.0.7+9.1/Contents/Home"

# Add to your shell profile
echo "export GRAALVM_HOME=$GRAALVM_HOME" >> ~/.zshrc
echo "export PATH=\$GRAALVM_HOME/bin:\$PATH" >> ~/.zshrc
echo "export JAVA_HOME=\$GRAALVM_HOME" >> ~/.zshrc

# Source the profile
source ~/.zshrc

# Install native-image
$GRAALVM_HOME/bin/gu install native-image

echo "GraalVM setup complete! Please restart your terminal or run 'source ~/.zshrc'" 