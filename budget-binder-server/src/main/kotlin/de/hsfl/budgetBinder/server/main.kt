package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.server.models.Roles
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.models.Users
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

fun main() = runBlocking<Unit> {
    val port = Integer.parseInt(System.getenv("PORT") ?: "8080")
    val host = System.getenv("HOST") ?: "0.0.0.0"


    val dbType = System.getenv("DB_TYPE")
    val dbServer = System.getenv("DB_SERVER")
    val dbPort = System.getenv("DB_PORT")
    val dbDatabaseName = System.getenv("DB_DATABASE_NAME")
    val dbUser = System.getenv("DB_USER") ?: ""
    val dbPassword = System.getenv("DB_PASSWORD") ?: ""

    val url: String
    val driver: String
    when (dbType) {
        "SQLITE" -> {
            // Database.connect("jdbc:sqlite:file:test?mode=memory&cache=shared", "org.sqlite.JDBC")
            val path = (System.getenv("SQLITE_PATH") ?: (System.getProperty("user.dir") + "/data")) + "/data.db"
            url = "jdbc:sqlite:$path"
            driver = "org.sqlite.JDBC"
        }
        "MYSQL" -> {
            // Database.connect("jdbc:mysql://localhost:3306/test", driver = "com.mysql.cj.jdbc.Driver",
            //      user = "root", password = "your_pwd")
            url = "jdbc:mysql://$dbServer:$dbPort/$dbDatabaseName"
            driver = "com.mysql.cj.jdbc.Driver"
        }
        "POSTGRESQL" -> {
            url = "jdbc:pgsql://$dbServer:$dbPort/$dbDatabaseName"
            driver = "com.impossibl.postgres.jdbc.PGDriver"
        }
        else -> {
            throw Exception("No DATABASE Type given")
        }
    }

    Database.connect(url, driver, user = dbUser, password = dbPassword)


    transaction {
        // Logging for DEV purposes
        addLogger(StdOutSqlLogger)

        SchemaUtils.create(Users)

        var root: UserEntity
        try {
            root = UserEntity[1]
        } catch (_: EntityNotFoundException) {
            val rootUserEmail = System.getenv("ROOT_USER_EMAIL")
            val rootUserPassword = System.getenv("ROOT_USER_PASSWORD")
            Users.insert {
                it[id] = 1
                it[name] = "Administrator"
                it[firstName] = "root"
                it[email] = rootUserEmail
                it[passwordHash] = BCrypt.hashpw(rootUserPassword, BCrypt.gensalt())
                it[role] = Roles.ADMIN
            }
            root = UserEntity[1]
        }
        if (root.role != Roles.ADMIN)
            throw Exception("First User is not the Root user")
    }
    /*
    * configure = {
    *   https://ktor.io/docs/engines.html#engine-main-configure
    *   https://api.ktor.io/ktor-server/ktor-server-netty/io.ktor.server.netty/-netty-application-engine/-configuration/index.html#2119802284%2FProperties%2F1117634132
    *   requestQueueLimit = 16
    *   shareWorkGroup = false
    *   configureBootstrap = {
    *       // ...
    *   }
    *   responseWriteTimeoutSeconds = 10
    * }
    * */
    embeddedServer(Netty, port = port, watchPaths = listOf("build/classes", "build/resources"), host = host) {
        module()
    }.start(wait = true)
}
