# Template Service

Basic Template for Spring Boot Web Service, using version 3.0.5, with basic authentication, MySql database, Open Feign and Swagger.
Compatible with native builds.

# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.5/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.5/maven-plugin/reference/html/#build-image)
* [GraalVM Native Image Support](https://docs.spring.io/spring-boot/docs/3.0.5/reference/html/native-image.html#native-image)
* [Eureka Discovery Client](https://docs.spring.io/spring-cloud-netflix/docs/current/reference/html/#service-discovery-eureka-clients)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.0.5/reference/htmlsingle/#data.sql.jpa-and-spring-data)
* [Validation](https://docs.spring.io/spring-boot/docs/3.0.5/reference/htmlsingle/#io.validation)
* [Spring Data JDBC](https://docs.spring.io/spring-boot/docs/3.0.5/reference/htmlsingle/#data.sql.jdbc)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/3.0.5/reference/htmlsingle/#appendix.configuration-metadata.annotation-processor)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.0.5/reference/htmlsingle/#web)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/3.0.5/reference/htmlsingle/#using.devtools)

### Guides

The following guides illustrate how to use some features concretely:

* [Service Registration and Discovery with Eureka and Spring Cloud](https://spring.io/guides/gs/service-registration-and-discovery/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)
* [Using Spring Data JDBC](https://github.com/spring-projects/spring-data-examples/tree/master/jdbc/basics)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Additional Links

These additional references should also help you:

* [Configure AOT settings in Build Plugin](https://docs.spring.io/spring-boot/docs/3.0.5/maven-plugin/reference/htmlsingle/#aot)

## GraalVM Native Support

This project has been configured to let you generate either a lightweight container or a native executable.
It is also possible to run your tests in a native image.

### Lightweight Container with Cloud Native Buildpacks

If you're already familiar with Spring Boot container images support, this is the easiest way to get started.
Docker should be installed and configured on your machine prior to creating the image.

To create the image, run the following goal:

```shell
$ export TAG_NAME="1.0.0-SNAPSHOT" && ./mvnw spring-boot:build-image -Pnative
```

Then, you can run the app like any other container:

```shell
$ docker run --rm -p 9090:9090 template-service:1.0.0
```

if you want to build and run:
```shell
$ export TAG_NAME="1.0.0-SNAPSHOT" && ./mvnw spring-boot:build-image -Pnative && docker run --rm -p 9090:9090 egripp/spring3-native-template/template-service:$TAG_NAME
```

### Executable with Native Build Tools

Use this option if you want to explore more options such as running your tests in a native image.
The GraalVM `native-image` compiler should be installed and configured on your machine.

NOTE: GraalVM 22.3+ is required.

To create the executable, run the following goal:

```shell
$ ./mvnw native:compile -Pnative
```

Then, you can run the app as follows:

```shell
$ target/coupons-service
```

You can also run your existing tests suite in a native image.
This is an efficient way to validate the compatibility of your application.

To run your existing tests in a native image, run the following goal:

```shell
$ ./mvnw test -PnativeTest
```

## DB

It connects to mysql db running at port 3306 with the following credentials
```text
    username: root
    password: example
```

You can change it in the ```application.yml``` in the resources folder

If you don't have a mysql db in your machine you can also do it using docker, execute in the root:
```shell
docker-compose up -d db-mysql
```

This will create a container with mysql db, the ```docker-compose.yaml``` is already configured with port user and password for this project.

To enable the communication between your db container and your application container you will need to create docker network:

```shell
docker network create my-network
```

Then connect the db to the network:
```shell
docker network connect my-network db-mysql
```

And finally, build the image and run it connecting to the network:

```shell
$ export TAG_NAME="1.0.0-SNAPSHOT" && ./mvnw spring-boot:build-image -Pnative -DskipTests && docker run --rm --network db-mysql -p 9090:9090 egripp/spring3-native-template/template-service:$TAG_NAME
```

## Build para Mac ARM (Apple Silicon) e Windows/Linux (x86_64)

> ⚠️ **DISCLAIMER SOBRE A IMAGEM DE BUILD:**
>
> O `pom.xml` já vem configurado de fábrica com a tag `<builder>arturds/builder-arm64:latest</builder>`.
> Essa imagem possui o compilador **Java 25** configurado via `buildpacks` especificamente para processadores **Apple Silicon (ARM64)**, evitando bugs crônicos de manifestos no Docker Desktop do Mac.
>
> 🔻 **Se você não está usando um Mac ARM (ex: Windows, Linux x86_64, ou Mac Intel):** 🔻
> Você precisará substituir o `<builder>` no seu `pom.xml` para a versão x86_64 oficial, ou passar via parâmetro:
> ```sh
> ./mvnw spring-boot:build-image -Pnative -DskipTests -Dspring-boot.build-image.builder=paketobuildpacks/builder-jammy-base:latest
> ```

Se você está usando um Mac M1/M2/M3, basta acionar a fase de Build Native sem precisar de nenhum hack adicional (a imagem base predefinida já funciona maravilhosamente bem com Java 25 Native):

```sh
./mvnw spring-boot:build-image -Pnative -DskipTests
```

Depois, rode normalmente com o Docker Compose ou o comando:

```sh
docker run --rm --network template-service_default -p 9090:9090 -e SPRING_PROFILES_ACTIVE=dev egripp/spring3-native-template/template-service:1.0.0
```

> Se você tentar rodar uma imagem x86_64 em um Mac ARM, verá o erro `failed to open elf at /lib64/ld-linux-x86-64.so.2`. Sempre gere a imagem para a arquitetura correta!

### Troubleshooting

Se você ainda encontrar o erro `rosetta error: failed to open elf at /lib64/ld-linux-x86-64.so.2` mesmo após usar o comando acima, verifique se:

- O parâmetro `-Dspring-boot.build-image.platform=linux/arm64` foi realmente aplicado durante o build.
- A imagem gerada foi corretamente taggeada para ARM. Você pode verificar isso com o comando:

```sh
docker inspect egripp/spring3-native-template/template-service:1.0.0 | grep Architecture
```

Se a arquitetura não estiver correta, tente limpar o cache do Docker e reconstruir a imagem:

```sh
docker builder prune -f
./mvnw clean
./mvnw spring-boot:build-image -Pnative -DskipTests \
  -Dspring-boot.build-image.imageName=egripp/spring3-native-template/template-service:1.0.0 \
  -Dspring-boot.build-image.builder=paketobuildpacks/builder-jammy-base:0.4.342 \
  -Dspring-boot.build-image.platform=linux/arm64
```

# Passo a passo para Lambda Native

## 1. Gerar a imagem nativa (ajuste para Mac ARM se necessário)

## 1. Gerar a imagem nativa

```sh
SPRING_PROFILES_ACTIVE=lambda ./mvnw spring-boot:build-image -Pnative -DskipTests \
  -Dspring-boot.build-image.imageName=egripp/spring3-native-template/template-service:1.0.0
```

## 2. Executar o container para gerar o binário (em background)

```sh
docker-compose up -d template
```

## 3. Extrair o binário nativo do container rodando

```sh
docker cp template:/workspace/com.example.template.TemplateServiceApplication target/template-service
```

## 4. Criar a imagem Lambda

```sh
docker build -t template-service-lambda:latest -f Dockerfile.lambda .
```

## 5. Rodar o teste local Lambda apontando para o MySQL do host

```sh
 docker run -e SPRING_PROFILES_ACTIVE=lambda \
  -e _HANDLER=org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest \
  -p 9000:8080 template-service-lambda:latest

```

## 6. Invocar o Lambda localmente

Em outro terminal:

```sh
curl -XPOST "http://localhost:9000/2015-03-31/functions/function/invocations" -d @test-event.json
```

> Certifique-se de que o MySQL está rodando localmente na porta 3306.

---

# Apêndice: Como atualizar a nossa Imagem de Builder (arturds/builder-arm64)

Caso a comunidade disponibilize uma nova versão do Java (ex: Java 26 ou 27) e você precise construir um novo *Paketo Builder* adaptado para os Macs do projeto, siga este passo a passo:

1. **Instale a ferramenta oficial `pack`:**
   No Mac, acesse via terminal e rode:
   ```bash
   brew install buildpacks/tap/pack
   ```
2. **Edite o `builder.toml` na raiz do projeto:**
   O arquivo `builder.toml` contém as prescrições de como o builder é empacotado. Altere a `version` associada ao `java-native-image` para a numeração mais recente divulgada na [Paketo Releases](https://github.com/paketo-buildpacks/java-native-image/releases). 
   *(Certifique-se de alterar as linhas em `[[buildpacks]]` e em `[[order.group]]` para baterem).*
3. **Recompile a imagem localmente forçando arquitetura arm64:**
   ```bash
   pack builder create arturds/builder-arm64:latest --config builder.toml
   ```
4. **Envie para o repositório público (Docker Hub):**
   ```bash
   docker login
   # Substitua :java25 pela nova versão, por exemplo :java27
   docker tag arturds/builder-arm64:latest arturds/builder-arm64:java123
   docker push arturds/builder-arm64:java123
   docker push arturds/builder-arm64:latest
   ```
Feito isto, todos do projeto receberão a nova versão da máquina de compilação quando fizerem builds.