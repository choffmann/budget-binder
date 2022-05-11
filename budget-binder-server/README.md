# Budget-Binder Server

This is the backend implementation for Budget-Binder.
It uses Ktor-Server as the Rest-API and Jetbrains/Exposed as Database ORM.
In addition, it uses Kodein for DI and bcrypt as Password Hashing Algorithm.

## Environment

The following Environment Variables are possible:
```
DEV=""
HOST=0.0.0.0
PORT=8080
FRONTEND_ADDRESS="" required
JWT_ACCESS_SECRET="" required
JWT_ACCESS_MINUTES=15
JWT_REFRESH_SECRET="" required
JWT_REFRESH_DAYS=7
ROOT_USER_EMAIL="" required
ROOT_USER_PASSWORD="" required
DB_TYPE="MYSQL | POSTGRESQL | SQLITE" required

Depending on DB_TYPE
SQLITE_PATH="app/data"
DB_SERVER=""
DB_PORT=""
DB_DATABASE_NAME=""
DB_USER=""
DB_PASSWORD=""
```

## Development Setup

First set up your env-variables to your system or for each individual gradle Task if you use an IDEA like IntelliJ IDEA for example:
```
DEV=True
JWT_ACCESS_SECRET=secret
JWT_REFRESH_SECRET=secret2
ROOT_USER_EMAIL=root@admin.com
ROOT_USER_PASSWORD=test
DB_TYPE=SQLITE
```

use the gradleTasks:
- :budget-binder-common:build -t
- :budget-binder-server:build -t
- :budget-binder-server:run

with IntelliJ IDEA or use them with gradlew/gradlew.bat in the Terminal

with this setup budget-binder-common and budget-binder-server are recompiled if you change the code and budget-binder-server with the env-variable DEV=True auto-reloads when new classes are built.

## Build and Deploy

There are two main ways to deploy this server. 

Use docker:
- in the project root `docker build -t budget-binder-server .`
- and then run the container like
```bash
docker run -it -p 8080:8080 -v ~/data:app/data \
-e DB_TYPE=SQLITE \
-e FRONTEND_ADDRESS= \
-e JWT_ACCESS_SECRET= \
-e JWT_REFRESH_SECRET= \
-e ROOT_USER_EMAIL= \
-e ROOT_USER_PASSWORD= \
-e DB_TYPE=SQLITE \
budget-binder-server
```

Or you build the jar on your machine:
- with :budget-binder-server:shadowJar
- copy the `budget-binder-server/build/libs/budget-binder-server-1.0-SNAPSHOT-all.jar` on the server
- set up the env-variables from the top
- run it with `java -jar budget-binder-server-1.0-SNAPSHOT-all.jar`

## Configure SSL
the easiest way to get SSL is to get a reverse proxy that handles it for you
for this you don't need anything else to specify

in development, you only need to specify the environment variable `DEV` on every start there will be a new created certificate

if you still want to do ssl with this server by its own you need a certificate for your domain from somewhere like `lets encrypt` and convert it to a jks-File

```bash
openssl pkcs12 -export -in cert.pem -inkey key.pem -out keystore.p12 -name "Budget Binder Server"

# You will be prompted to enter a passphrase for key.pem and a new password for keystore.p12.

keytool -importkeystore -srckeystore keystore.p12 -srcstoretype pkcs12 -destkeystore keystore.jks

# You will be prompted to enter a password for the keystore.p12 file and a new password for keystore.jks. 
# The keystore.jks will be generated.
```

Then you need to set the ENV `SSL` to `True` and `KEYSTORE_PATH` to the file path as well as `KEYSTORE_PASSWORD` with the specified passphrases. At last, you can set `NO_FORWARD_HEADER` to disable the forward header support.
