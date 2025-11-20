#!/bin/bash

# Load environment variables from .env file if it exists
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
fi

# Remove existing container if it exists
docker rm -f dev-postgres 2>/dev/null

# Build volume args
VOLUME_ARGS="-v $(pwd)/scripts/schema.sql:/docker-entrypoint-initdb.d/01-schema.sql"

# Check if sample_data.sql exists, and add it to the volumes if so
if [ -f "$(pwd)/scripts/sample_data.sql" ]; then
  VOLUME_ARGS+=" -v $(pwd)/scripts/sample_data.sql:/docker-entrypoint-initdb.d/02-sample_data.sql"
fi

# Run PostgreSQL container with conditional volumes
docker run --name dev-postgres \
  -e POSTGRES_USER=devuser \
  -e POSTGRES_PASSWORD=devpass \
  -e POSTGRES_DB=dev-jobboard-db \
  $VOLUME_ARGS \
  -p 5432:5432 \
  -d postgres:16

# Wait a few seconds for PostgreSQL to initialize
echo "Waiting for PostgreSQL to initialize..."
sleep 5

# Build and run the Java backend in development mode
echo "Starting Java backend in development mode..."
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
