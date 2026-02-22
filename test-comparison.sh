#!/bin/bash

# Função para medir o tempo de execução de uma requisição Lambda
measure_lambda_execution() {
    local request_num=$1
    echo "Running Lambda request $request_num..."
    start_time=$(date +%s.%N)
    
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
    execution_time=$(echo "$end_time - $start_time" | bc)
    echo "$execution_time"
}

# Função para medir o tempo de execução de uma requisição HTTP normal
measure_http_execution() {
    local request_num=$1
    local port=$2
    echo "Running HTTP request $request_num..."
    start_time=$(date +%s.%N)
    
    curl -X GET "http://localhost:$port/template?page=1&pageSize=10&sort=asc&sortField=id" \
         -H "accept: */*" \
         -H "Authorization: Basic YWRtaW46YWRtaW4="
    
    end_time=$(date +%s.%N)
    execution_time=$(echo "$end_time - $start_time" | bc)
    echo "$execution_time"
}

# Função para executar os testes
run_tests() {
    local test_name=$1
    local start_command=$2
    local measure_command=$3
    local port=$4
    
    echo "=== Running $test_name Tests ==="
    echo "Starting application..."
    
    # Iniciar a aplicação
    eval "$start_command" > app.log 2>&1 &
    APP_PID=$!
    
    # Esperar a aplicação iniciar
    while ! grep -q "Started" app.log; do
        sleep 1
    done
    
    # Array para armazenar os tempos
    declare -a times
    
    # Executar 5 requisições
    for i in {1..5}; do
        if [ "$measure_command" = "lambda" ]; then
            time=$(measure_lambda_execution $i)
        else
            time=$(measure_http_execution $i $port)
        fi
        times+=($time)
        echo "Request $i: $time seconds"
        sleep 2
    done
    
    # Calcular médias
    cold_start=${times[0]}
    warm_starts=("${times[@]:1}")
    warm_start_sum=0
    for time in "${warm_starts[@]}"; do
        warm_start_sum=$(echo "$warm_start_sum + $time" | bc)
    done
    warm_start_avg=$(echo "scale=3; $warm_start_sum / ${#warm_starts[@]}" | bc)
    
    # Limpar
    kill $APP_PID
    rm app.log
    
    # Retornar resultados
    echo "$cold_start $warm_start_avg"
}

# Download RIE se não existir
if [ ! -f aws-lambda-rie ]; then
    echo "Downloading AWS Lambda Runtime Interface Emulator..."
    curl -Lo aws-lambda-rie https://github.com/aws/aws-lambda-runtime-interface-emulator/releases/latest/download/aws-lambda-rie
    chmod +x aws-lambda-rie
fi

# Build do projeto
echo "Building project..."
./mvnw clean package -DskipTests

# Build da versão native
echo "Building native image..."
./mvnw clean package -Pnative -DskipTests

# Executar testes
echo "Running comparison tests..."

# Teste 1: Spring Boot tradicional
spring_results=$(run_tests "Spring Boot" \
    "SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run" \
    "http" \
    "9091")

# Teste 2: Spring Boot Lambda
lambda_results=$(run_tests "Spring Boot Lambda" \
    "SPRING_PROFILES_ACTIVE=local java -jar target/template-service-1.0.0-aws.jar" \
    "lambda" \
    "8080")

# Teste 3: Spring Native Lambda
native_results=$(run_tests "Spring Native Lambda" \
    "SPRING_PROFILES_ACTIVE=local ./aws-lambda-rie target/template-service" \
    "lambda" \
    "8080")

# Gerar relatório
echo -e "\n=== Relatório de Performance ==="
echo -e "\nTempos em segundos:"
echo "----------------------------------------"
echo "Cenário                Cold Start    Warm Start (média)"
echo "----------------------------------------"
echo "Spring Boot           ${spring_results}"
echo "Spring Boot Lambda    ${lambda_results}"
echo "Spring Native Lambda  ${native_results}"
echo "----------------------------------------" 