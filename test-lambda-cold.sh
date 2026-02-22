#!/bin/bash

# Build do projeto
echo "Building project..."
./mvnw clean package -DskipTests

echo "=== Running Lambda Cold Start Test ==="
echo "Starting Lambda function..."

# Iniciar a aplicação Lambda
SPRING_PROFILES_ACTIVE=local java -jar target/template-service-1.0.0-aws.jar > app.log 2>&1 &
APP_PID=$!

# Esperar a aplicação iniciar
while ! grep -q "Started" app.log; do
    sleep 1
done

# Medir o tempo total de inicialização
start_time=$(date +%s.%N)

# Executar a requisição Lambda
echo "Running Lambda request..."
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
total_time=$(echo "$end_time - $start_time" | bc)

# Limpar
kill $APP_PID
rm app.log

echo "Cold Start Time: $total_time seconds" 