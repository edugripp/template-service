FROM ubuntu:22.04

WORKDIR /app

# Copy the native executable
COPY target/template-service /app/template-service

# Make it executable
RUN chmod +x /app/template-service

# Expose the application port
EXPOSE 9090

# Run the native executable
ENTRYPOINT ["/app/template-service"]