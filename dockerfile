FROM maven:3.6.1-jdk-8-slim AS build
RUN mkdir -p /workspace
WORKDIR /workspace
COPY pom.xml /workspace
COPY src /workspace/src
RUN mvn -f pom.xml clean package

FROM openjdk:8-alpine
RUN mkdir -p /home/backend
COPY --from=build /workspace/target/*.jar /home/backend/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/home/backend/app.jar"]