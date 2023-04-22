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