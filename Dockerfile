FROM gradle:7-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src/budget-binder-server
RUN gradle shadowJar --no-daemon

FROM openjdk:17
RUN mkdir -p /app/data
WORKDIR /app

VOLUME /app/data
COPY --from=build /home/gradle/src/budget-binder-server/build/libs/*.jar /app/ktor-docker-server.jar

ENV \
HOST=0.0.0.0 \
PORT=8080 \
FRONTEND_ADDRESS="" \
JWT_ACCESS_SECRET="" \
JWT_ACCESS_MINUTES=15 \
JWT_REFRESH_SECRET="" \
JWT_REFRESH_DAYS=7 \
DB_TYPE="MYSQL | POSTGRESQL | SQLITE" \
SQLITE_PATH="/app/data" \
DB_SERVER="" \
DB_PORT="" \
DB_DATABASE_NAME="" \
DB_USER="" \
DB_PASSWORD=""

ENTRYPOINT ["java","-jar","/app/ktor-docker-server.jar"]