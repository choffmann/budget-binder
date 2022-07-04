# Budget-Binder Server

This is the backend implementation for Budget-Binder.
It uses `Ktor-Server` as the Rest-API and `Jetbrains/Exposed` as Database ORM.
In addition, it uses `Kodein` for DI and bcrypt as Password Hashing Algorithm.
`CliKt` is used for Argument-parsing and `Hoplite` to parse a config-file.

The server can also be used to deploy the JS-Client. 

With Docker you can deploy this Server very easy.
You only need to copy the [config-sample.yaml](https://github.com/choffmann/budget-binder/blob/main/budget-binder-server/data/config_sample.yaml) as config.yaml and edit accessSecret and refreshSecret. Then deploy it like:
```bash
docker run -dit --restart unless-stopped -p 8080:8080 \
-v $(pwd)/config.yaml:/app/config.yaml \
akatranlp/budget-binder-server:1.0.0 -conf config.yaml
```

This will start the Server on Port 8080 and save the data in a SQLITE Database which is saved in a docker volume.

## Config

You can config this server in 2 ways:
- with a config-file that is provided via commandline Argument `-conf, --configFile`
- via environment Variables

all possible config-Values are shown in the [config-sample.yaml](https://github.com/choffmann/budget-binder/blob/main/budget-binder-server/data/config_sample.yaml)

and all possible ENV-Variables are:
```
DEV=False
SSL=False
HOST=0.0.0.0
PORT=8080
SSL_HOST=0.0.0.0
SSL_PORT=8443
KEYSTORE_PASSWORD=secret
KEYSTORE_PATH="data/keystore.jks"
NO_FORWARDED_HEADER=False
DB_TYPE="MYSQL | POSTGRESQL | SQLITE"
SQLITE_PATH="data/data.db"
DB_SERVER=""
DB_PORT=""
DB_DATABASE_NAME=""
DB_USER=""
DB_PASSWORD=""
JWT_ACCESS_SECRET=""
JWT_REFRESH_SECRET=""
JWT_ACCESS_MINUTES=15
JWT_REFRESH_DAYS=7
JWT_REALM=budget-binder
JWT_ISSUER="http://0.0.0.0:8080"
JWT_AUDIENCE="http://0.0.0.0:8080"
```

## Development Setup

First copy the config-sample.yaml to config.yaml and configure it (the standard should be enough).

Then create tasks to create the JS-Client
```bash
./gradlew :budget-binder-multiplatform-app:jsBrowserDistribution
```
Copy it with the CopyScript into the public-Folder
```bash
./CopyScriptJsMain.sh
```
And set the ENV DEV=True and run the server with the created configFile 
```bash
export DEV=True && \
./gradlew :budget-binder-server:run --args"-conf data/config.yaml"
```
All the previous steps can be configured in your IDE like Intellij-IDEA or with a separate script like
```bash
export DEV=True && \
./gradlew :budget-binder-multiplatform-app:jsBrowserDistribution && \
./CopyScriptJsMain.sh && \
./gradlew :budget-binder-server:run --args"-conf data/config.yaml"
```

## Build and Deploy

There are two main ways to deploy this server and the JS-Client. 

Use docker:
- in the project root `docker build -t budget-binder-server .`
- and then run the container like
```bash
docker run -it -p 8080:8080 -v ~/data:app/data \
-e DB_TYPE=SQLITE \
-e FRONTEND_ADDRESSES= \
-e JWT_ACCESS_SECRET= \
-e JWT_REFRESH_SECRET= \
-e DB_TYPE=SQLITE \
budget-binder-server
```

Or you build the jar and JS-Client on your machine:
- with :budget-binder-server:shadowJar :budget-binder-multiplatform-app:jsBrowserDistribution
- copy the `budget-binder-server/build/libs/budget-binder-server-1.0-SNAPSHOT-all.jar` on the server
- create a subfolder `public` in the directory of the server-jar
- copy `budget-binder-multiplatform-app/build/distributions/*` into it
- set up the config
- run it with `java -jar budget-binder-server-1.0-SNAPSHOT-all.jar (-conf config.yaml)`

## Configure SSL
the easiest way to get SSL is to get a reverse proxy that handles it for you
for this you don't need anything else to specify

in development, you only need to specify in the config `DEV=True` and `SSL=True` on every start there will be a new created certificate

if you still want to do ssl with this server by its own you need a certificate for your domain from somewhere like `lets encrypt` and convert it to a jks-File

```bash
openssl pkcs12 -export -in cert.pem -inkey key.pem -out keystore.p12 -name "Budget Binder Server"

# You will be prompted to enter a passphrase for key.pem and a new password for keystore.p12.

keytool -importkeystore -srckeystore keystore.p12 -srcstoretype pkcs12 -destkeystore keystore.jks

# You will be prompted to enter a password for the keystore.p12 file and a new password for keystore.jks. 
# The keystore.jks will be generated.
```

Then you need to set `SSL=True` in the config and `KEYSTORE_PATH` to the file path as well as `KEYSTORE_PASSWORD` with the specified passphrases. At last, you can set `NO_FORWARD_HEADER` to disable the forward header support.
