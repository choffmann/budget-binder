package de.hsfl.budgetBinder.server

import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    val port = Integer.parseInt(System.getenv("PORT") ?: "8080")
    val host = System.getenv("HOST") ?: "0.0.0.0"

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
    embeddedServer(
        Netty,
        host = host,
        port = port,
        module = Application::module,
        watchPaths = listOf("build/classes", "build/resources")
    ).start(wait = true)
}
