FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src/budget-binder-server
RUN gradle shadowJar --no-daemon

FROM openjdk:11
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/gradle/src/budget-binder-server/build/libs/*.jar /app/ktor-docker-server.jar
ENTRYPOINT ["java","-jar","/app/ktor-docker-server.jar"]