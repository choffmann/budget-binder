FROM gradle:7-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle :budget-binder-multiplatform-app:jsBrowserDistribution :budget-binder-server:shadowJar --no-daemon

FROM openjdk:17
RUN mkdir -p /app/data && mkdir -p /app/public
WORKDIR /app

VOLUME /app/data
COPY --from=build /home/gradle/src/budget-binder-server/public /app/public/
COPY --from=build /home/gradle/src/budget-binder-multiplatform-app/build/distributions/ /app/public/
COPY --from=build /home/gradle/src/budget-binder-server/build/libs/*.jar /app/ktor-docker-server.jar

ENV \
SSL=True \
HOST=0.0.0.0 \
PORT=8080 \
SSL_HOST=0.0.0.0 \
SSL_PORT=8443 \
KEYSTORE_PASSWORD=secret \
KEYSTORE_PATH="data/keystore.jks" \
FRONTEND_ADDRESSES="" \
NO_FORWARDED_HEADER=False \
DB_TYPE="MYSQL | POSTGRESQL | SQLITE" \
SQLITE_PATH="data/data.db" \
DB_SERVER="" \
DB_PORT="" \
DB_DATABASE_NAME="" \
DB_USER="" \
DB_PASSWORD="" \
JWT_ACCESS_SECRET="" \
JWT_REFRESH_SECRET="" \
JWT_ACCESS_MINUTES=15 \
JWT_REFRESH_DAYS=7 \
JWT_REALM=budget-binder \
JWT_ISSUER="http://0.0.0.0:8080" \
JWT_AUDIENCE="http://0.0.0.0:8080"

ENTRYPOINT ["java","-jar","/app/ktor-docker-server.jar"]
