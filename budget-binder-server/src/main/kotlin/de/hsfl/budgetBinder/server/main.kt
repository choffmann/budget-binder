package de.hsfl.budgetBinder.server

import io.ktor.application.*
import io.ktor.network.tls.certificates.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking
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

    val dev = System.getenv("DEV") != null
    val keyStoreFile: File?
    val keystore: KeyStore?
    if (dev) {
        keyStoreFile = File("data/keystore.jks")
        keystore = generateCertificate(
            file = keyStoreFile,
            keyAlias = "sampleAlias",
            keyPassword = "foobar",
            jksPassword = "foobar"
        )
    } else {
        keyStoreFile = null
        keystore = null
    }
    val environment = applicationEngineEnvironment {
        // log = LoggerFactory.getLogger("ktor.application")
        connector {
            port = Integer.parseInt(System.getenv("PORT") ?: "8080")
            host = System.getenv("HOST") ?: "0.0.0.0"
        }
        if (dev) {
            sslConnector(
                keyStore = keystore!!,
                keyAlias = "sampleAlias",
                keyStorePassword = { "foobar".toCharArray() },
                privateKeyPassword = { "foobar".toCharArray() }) {
                port = 8443
                keyStorePath = keyStoreFile
            }
        }
        module(Application::module)
        watchPaths = listOf("build/classes", "build/resources")
    }

    embeddedServer(Netty, environment).start(wait = true)
}
