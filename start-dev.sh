#!/bin/bash

# Load environment variables from .env file
# This properly handles values with spaces
while IFS='=' read -r key value; do
    # Skip comments and empty lines
    [[ $key =~ ^#.*$ ]] && continue
    [[ -z "$key" ]] && continue

    # Remove quotes if present
    value="${value%\"}"
    value="${value#\"}"

    # Export the variable
    export "$key=$value"
done < .env

# Start Spring Boot application
./mvnw spring-boot:run
