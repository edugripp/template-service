FROM dashaun/builder:tiny-java-21 AS builder

WORKDIR /app
COPY . .

ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64

RUN ./mvnw clean package -Pnative -DskipTests

FROM scratch
COPY --from=builder /app/target/template-service /app/template-service
ENTRYPOINT ["/app/template-service"]