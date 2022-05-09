package de.hsfl.budgetBinder.server

object Config {
    enum class SSLState {
        DEV,
        SSL,
        NONE,
    }

    enum class DBType {
        SQLITE,
        MYSQL,
        POSTGRES,
    }

    val dbType: DBType
    val sqlitePath: String
    val dbServerAddress: String
    val dbServerPort: String
    val dbName: String
    val dbUser: String
    val dbPassword: String

    val dev: Boolean
    val sslState: SSLState
    val host: String
    val sslHost: String
    val port: Int
    val sslPort: Int
    val keyStorePassword: String
    val keyStorePath: String

    val frontendAddresses: List<String>

    val jwtAccessSecret: String
    val jwtRefreshSecret: String
    val jwtAccessMinutes: Int
    val jwtRefreshDays: Int
    val jwtIssuer: String
    val jwtAudience: String


    init {
        val dbEnv = System.getenv("DB_TYPE") ?: throw Exception("No Database Type given")
        dbType = when (dbEnv) {
            "SQLITE" -> DBType.SQLITE
            "MYSQL" -> DBType.MYSQL
            "POSTGRES" -> DBType.POSTGRES
            else -> throw Exception("Not Supported Database Type given: $dbEnv")
        }

        if (dbType == DBType.SQLITE) {
            sqlitePath = (System.getenv("SQLITE_PATH") ?: (System.getProperty("user.dir") + "/data")) + "/data.db"
            dbServerAddress = ""
            dbServerPort = ""
            dbName = ""
            dbUser = ""
            dbPassword = ""
        } else {
            sqlitePath = ""
            dbServerAddress = System.getenv("DB_SERVER") ?: throw Exception("No dbServerAddress specified")
            dbServerPort = System.getenv("DB_PORT") ?: throw Exception("No dbServerPort specified")
            dbName = System.getenv("DB_DATABASE_NAME") ?: throw Exception("No dbDatabaseName specified")
            dbUser = System.getenv("DB_USER") ?: throw Exception("No dbUser specified")
            dbPassword = System.getenv("DB_PASSWORD") ?: throw Exception("No dbPassword specified")
        }

        jwtAccessSecret = System.getenv("JWT_ACCESS_SECRET") ?: throw Exception("No AccessTokenSecret provided")
        jwtRefreshSecret = System.getenv("JWT_REFRESH_SECRET") ?: throw Exception("No RefreshTokenSecret provided")
        jwtAccessMinutes = Integer.parseInt(System.getenv("JWT_ACCESS_MINUTES") ?: "15")
        jwtRefreshDays = Integer.parseInt(System.getenv("JWT_REFRESH_DAYS") ?: "7")
        jwtIssuer = System.getenv("JWT_ISSUER") ?: "http://0.0.0.0:8080/"
        jwtAudience = System.getenv("JWT_AUDIENCE") ?: "http://0.0.0.0:8080/"

        frontendAddresses = System.getenv("FRONTEND_ADDRESSES")
            ?.replace(" ", "")
            ?.split(",")
            ?: listOf()

        host = System.getenv("HOST") ?: "0.0.0.0"
        sslHost = System.getenv("SSL_HOST") ?: "0.0.0.0"
        port = Integer.parseInt(System.getenv("PORT") ?: "8080")
        sslPort = Integer.parseInt(System.getenv("SSL_PORT") ?: "8443")

        val devEnv = System.getenv("DEV")
        dev = devEnv?.let { true } ?: false

        sslState = devEnv?.let {
            SSLState.DEV
        } ?: System.getenv("SSL")?.let {
            SSLState.SSL
        } ?: SSLState.NONE

        keyStorePassword = System.getenv("KEYSTORE_PASSWORD")
            ?: (if (sslState == SSLState.SSL) {
                throw Exception("No KeystorePassword provided")
            } else {
                "budget-binder-server"
            })

        keyStorePath = System.getenv("KEYSTORE_PATH")
            ?: (if (sslState == SSLState.SSL) {
                throw Exception("No KeystorePath provided")
            } else {
                "data/dev_keystore.jks"
            })
    }
}