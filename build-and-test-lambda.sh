#!/bin/bash

set -e

echo "Build da imagem Lambda (template-lambda)..."
docker build -t template-lambda -f Dockerfile.lambda .

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

echo "Criando rede Docker..."
docker network create lambda-network || true

echo "Subindo o banco de dados MySQL..."
docker run -d --rm --name mysql-test \
  --network lambda-network \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=template \
  -e MYSQL_USER=admin \
  -e MYSQL_PASSWORD=admin \
  mysql:8.0

echo "Aguardando o MySQL iniciar..."
sleep 10

echo "Subindo o container Lambda..."
docker run -d --rm \
  --name lambda-test \
  --network lambda-network \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql-test:3306/template \
  -e SPRING_DATASOURCE_USERNAME=admin \
  -e SPRING_DATASOURCE_PASSWORD=admin \
  -p 9000:8080 \
  template-lambda

sleep 5
echo "Testando Lambda Native..."
curl -XPOST "http://localhost:9000/2015-03-31/functions/function/invocations" -d @test-event.json

echo "Limpando..."
docker stop lambda-test mysql-test
docker network rm lambda-network
rm test-event.json 