FROM openjdk:22-slim-buster
EXPOSE 9090
ARG SPRING_PROFILES_ACTIVE=default
ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE
ENV JAVA_OPTIONS=$JAVA_OPTIONS
VOLUME /tmp
ADD /target/*.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]