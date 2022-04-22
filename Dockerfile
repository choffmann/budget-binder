FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src/budget-binder-server
RUN gradle shadowJar --no-daemon

FROM openjdk:11
RUN mkdir -p /app/data
WORKDIR /app

VOLUME /app/data
COPY --from=build /home/gradle/src/budget-binder-server/build/libs/*.jar /app/ktor-docker-server.jar

ENV \
HOST=0.0.0.0 \
PORT=8080 \
USE_SQLITE=True \
SQLITE_PATH="/app/data" \
DB_TYPE="MYSQL | POSTGRESQL" \
DB_SERVER="" \
DB_PORT="" \
DB_DATABASE_NAME="" \
DB_USER="" \
DB_PASSWORD=""

ENTRYPOINT ["java","-jar","/app/ktor-docker-server.jar"]