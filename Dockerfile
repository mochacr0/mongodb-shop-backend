# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk as base
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:resolve
COPY src src

FROM base as development
EXPOSE 5000
CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005'"]

FROM base as build
RUN ./mvnw package

FROM eclipse-temurin:17-jre-alpine as production
EXPOSE 5000
COPY --from=build /app/target/spring-boot-mongodb-*.jar /spring-boot-mongodb.jar
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/spring-boot-mongodb.jar"]