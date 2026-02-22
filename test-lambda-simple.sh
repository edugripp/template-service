#!/bin/bash

# Build do projeto
echo "Building project..."
./mvnw clean package -DskipTests

# Criar arquivo de teste
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

# Executar o Lambda
echo "Executing Lambda..."
time java -jar target/template-service-1.0.0-aws.jar --spring.profiles.active=lambda < test-event.json

# Limpar
rm test-event.json 