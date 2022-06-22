package de.hsfl.budgetBinder.server.config

import java.io.File


data class Config(val server: Server, val database: Database, val jwt: JWT) {
    data class Server(
        val dev: Boolean,
        val ssl: Boolean,
        val host: String,
        val port: Int,
        val sslHost: String,
        val sslPort: Int,
        val keyStorePassword: String,
        val keyStorePath: String,
        val forwardedHeaderSupport: Boolean
    )

    enum class DBType {
        SQLITE,
        MYSQL,
        POSTGRES,
    }

    data class Database(
        val dbType: DBType,
        val sqlitePath: String,
        val serverAddress: String,
        val serverPort: String,
        val name: String,
        val user: String,
        val password: String
    )

    data class JWT(
        val accessSecret: String,
        val refreshSecret: String,
        val accessMinutes: Int,
        val refreshDays: Int,
        val realm: String,
        val issuer: String,
        val audience: String
    )

    companion object {
        fun create(configFile: File? = null): Config {
            return configFile?.let {
                createFromIntermediate(ConfigIntermediate.createFromFile(it))
            } ?: createFromIntermediate(ConfigIntermediate.createFromEnv())
        }

        fun createFromIntermediate(intermediate: ConfigIntermediate): Config {
            val dbType = intermediate.database.dbType

            val sqlitePath: String
            val dbServerAddress: String
            val dbServerPort: String
            val dbName: String
            val dbUser: String
            val dbPassword: String
            if (dbType == DBType.SQLITE) {
                sqlitePath = intermediate.database.sqlitePath ?: (System.getProperty("user.dir") + "/data/data.db")
                dbServerAddress = ""
                dbServerPort = ""
                dbName = ""
                dbUser = ""
                dbPassword = ""
            } else {
                sqlitePath = ""
                dbServerAddress = intermediate.database.serverAddress ?: error("No dbServerAddress specified")
                dbServerPort = intermediate.database.serverPort ?: error("No dbServerPort specified")
                dbName = intermediate.database.name ?: error("No dbDatabaseName specified")
                dbUser = intermediate.database.user ?: error("No dbUser specified")
                dbPassword = intermediate.database.password ?: error("No dbPassword specified")
            }

            val dev = intermediate.server?.dev ?: false
            val ssl = intermediate.server?.ssl ?: false

            val host = intermediate.server?.host ?: "0.0.0.0"
            val port = intermediate.server?.port ?: 8080
            val sslHost = intermediate.server?.sslHost ?: "0.0.0.0"
            val sslPort = intermediate.server?.sslPort ?: 8443

            val keyStorePassword: String
            val keyStorePath: String
            if (ssl) {
                keyStorePassword = intermediate.server?.keyStorePassword
                    ?: if (dev) "budget-binder-server" else error("No KeystorePassword provided")
                keyStorePath = intermediate.server?.keyStorePath
                    ?: if (dev) "data/dev_keystore.jks" else error("No KeystorePath provided")
            } else {
                keyStorePassword = ""
                keyStorePath = ""
            }

            val forwardedHeaderSupport = !(intermediate.server?.noForwardedHeaderSupport ?: false)

            val jwtAccessSecret = intermediate.jwt.accessSecret
            val jwtRefreshSecret = intermediate.jwt.refreshSecret
            val jwtAccessMinutes = intermediate.jwt.accessMinutes ?: 15
            val jwtRefreshDays = intermediate.jwt.refreshDays ?: 7
            val jwtRealm = intermediate.jwt.realm ?: "budget-binder-server"
            val jwtIssuer = intermediate.jwt.issuer ?: "http://0.0.0.0:8080/"
            val jwtAudience = intermediate.jwt.audience ?: "http://0.0.0.0:8080/"

            return Config(
                database = Database(
                    dbType,
                    sqlitePath,
                    dbServerAddress,
                    dbServerPort,
                    dbName,
                    dbUser,
                    dbPassword
                ), server = Server(
                    dev,
                    ssl,
                    host,
                    port,
                    sslHost,
                    sslPort,
                    keyStorePassword,
                    keyStorePath,
                    forwardedHeaderSupport
                ), jwt = JWT(
                    jwtAccessSecret,
                    jwtRefreshSecret,
                    jwtAccessMinutes,
                    jwtRefreshDays,
                    jwtRealm,
                    jwtIssuer,
                    jwtAudience
                )
            )
        }
    }
}
