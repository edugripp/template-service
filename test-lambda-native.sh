#!/bin/bash

set -e

# Caminho do binário nativo
BINARY=target/template-service
RIE=aws-lambda-rie
PORT=8080

# Verifica se o binário existe
if [ ! -f "$BINARY" ]; then
  echo "Binário nativo não encontrado em $BINARY. Gerando com GraalVM..."
  ./mvnw clean package -Pnative -DskipTests
fi

# Detecta arquitetura
ARCH=$(uname -m)
RIE_URL="https://github.com/aws/aws-lambda-runtime-interface-emulator/releases/latest/download/aws-lambda-rie"
if [[ "$ARCH" == "arm64" || "$ARCH" == "aarch64" ]]; then
  RIE_URL+=".arm64"
fi

# Baixa o RIE se necessário
if [ ! -f "$RIE" ]; then
  echo "Baixando AWS Lambda Runtime Interface Emulator (RIE) para arquitetura $ARCH..."
  curl -Lo $RIE $RIE_URL
  chmod +x $RIE
fi

# Cria evento de teste
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

# Sobe o Lambda Native com o RIE em background
./$RIE ./$BINARY &
RIE_PID=$!

# Aguarda o serviço subir
sleep 2

echo "Enviando evento de teste para o Lambda Native..."
start_time=$(date +%s.%N)
RESPONSE=$(curl -s -XPOST "http://localhost:$PORT/2015-03-31/functions/function/invocations" -d @test-event.json)
end_time=$(date +%s.%N)
execution_time=$(echo "$end_time - $start_time" | bc)

echo "Resposta do Lambda Native:"
echo "$RESPONSE"
echo "Tempo de execução (cold start): $execution_time segundos"

# Finaliza o processo do RIE
kill $RIE_PID

# Limpar
rm test-event.json 