package de.hsfl.budgetBinder.server

import io.ktor.application.*
import io.ktor.network.tls.certificates.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.security.KeyStore

fun main() = runBlocking<Unit> {
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

    val sslState = System.getenv("DEV")?.let { "DEV" } ?: System.getenv("SSL")?.let { "SSL" } ?: "NONE"

    val keyStorePath = System.getenv("KEYSTORE_PATH")
    val keyStorePassword = System.getenv("KEYSTORE_PASSWORD") ?: "budget-binder"

    val keyStore = when (sslState) {
        "DEV" -> generateCertificate(
            file = File("data/dev_keystore.jks"),
            keyAlias = "Budget Binder Server",
            keyPassword = keyStorePassword,
            jksPassword = keyStorePassword
        )
        "SSL" -> withContext(Dispatchers.IO) {
            KeyStore.getInstance(File(keyStorePath), keyStorePassword.toCharArray())
        }
        else -> null
    }

    val environment = applicationEngineEnvironment {
        // log = LoggerFactory.getLogger("ktor.application")
        connector {
            port = Integer.parseInt(System.getenv("PORT") ?: "8080")
            host = System.getenv("HOST") ?: "0.0.0.0"
        }
        if (sslState == "SSL" || sslState == "DEV") {
            sslConnector(
                keyStore = keyStore!!,
                keyAlias = "Budget Binder Server",
                keyStorePassword = { keyStorePassword.toCharArray() },
                privateKeyPassword = { keyStorePassword.toCharArray() }) {
                port = Integer.parseInt(System.getenv("SSL_PORT") ?: "8443")
            }
        }
        module(Application::module)
        watchPaths = listOf("build/classes", "build/resources")
    }

    embeddedServer(Netty, environment).start(wait = true)
}
