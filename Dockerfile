FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ./target/exe_spring-0.0.1-SNAPSHOT.jar.original app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
