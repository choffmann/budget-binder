package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.server.models.Users
import de.hsfl.budgetBinder.server.routes.userRoutes
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.*
import io.ktor.server.auth.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

fun main() = runBlocking<Unit> {
    val port = Integer.parseInt(System.getenv("PORT") ?: "8080")
    val host = System.getenv("HOST") ?: "0.0.0.0"

    // Database.connect("jdbc:mysql://localhost:3306/test", driver = "com.mysql.cj.jdbc.Driver",
    //      user = "root", password = "your_pwd")
    // Gradle
    // implementation("mysql:mysql-connector-java:8.0.2")


    // Database.connect("jdbc:sqlite:file:test?mode=memory&cache=shared", "org.sqlite.JDBC")
    // implementation("org.xerial:sqlite-jdbc:3.30.1")
    Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")

    transaction {
        // Logging for DEV purposes
        addLogger(StdOutSqlLogger)

        SchemaUtils.create(Users)
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
    embeddedServer(Netty, port = port, watchPaths = listOf("classes", "resources"), host = host) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(CORS) {
        anyHost()
    }
    install(Authentication) {
        basic("auth-basic") {
            realm = "Budget Binder Server"
            validate {
                UserIdPrincipal(it.name)
            }
        }
    }

    install(ContentNegotiation) {
        json()
    }

    // install all Modules
    userRoutes()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}