FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /tmp/lib-build
RUN git clone https://github.com/RafaelPinheiroCosta/spring-mqttx.git .
RUN mvn versions:set -DnewVersion=1.1.0 -DgenerateBackupPoms=false
RUN mvn clean install -DskipTests

WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw
RUN ./mvnw -q -DskipTests dependency:go-offline
COPY src src
RUN ./mvnw -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx300m", "-Xss512k", "-jar", "/app/app.jar"]