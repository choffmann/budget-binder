package de.hsfl.budgetBinder.server

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import de.hsfl.budgetBinder.server.config.getServerConfig
import io.ktor.network.tls.certificates.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.security.KeyStore

class ServerMain : CliktCommand() {
    private val configFile: File? by option("--configFile", "-conf").file(
        mustExist = true,
        canBeDir = false,
        mustBeReadable = true
    )

    override fun run(): Unit = runBlocking {

        val config = getServerConfig(configFile)

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
            module {
                mainModule(config)
            }
            // log = LoggerFactory.getLogger("ktor.application")

            connector {
                host = System.getenv("HOST") ?: "0.0.0.0"
                port = Integer.parseInt(System.getenv("PORT") ?: "8080")
            }
            if (sslState == "SSL" || sslState == "DEV") {
                sslConnector(
                    keyStore = keyStore!!,
                    keyAlias = "Budget Binder Server",
                    keyStorePassword = { keyStorePassword.toCharArray() },
                    privateKeyPassword = { keyStorePassword.toCharArray() }
                ) {
                    host = System.getenv("SSL_HOST") ?: "0.0.0.0"
                    port = Integer.parseInt(System.getenv("SSL_PORT") ?: "8443")
                }
            }
            if (sslState == "DEV") {
                watchPaths = listOf("build/classes", "build/resources")
            }
        }

        embeddedServer(Netty, environment = environment, configure = {
            /*  https://ktor.io/docs/engines.html#engine-main-configure
            *   https://api.ktor.io/ktor-server/ktor-server-netty/io.ktor.server.netty/-netty-application-engine/-configuration/index.html#2119802284%2FProperties%2F1117634132
            *   requestQueueLimit = 16
            *   shareWorkGroup = false
            *   configureBootstrap = {
            *       // ...
            *   }
            *   responseWriteTimeoutSeconds = 10
            */
        }).start(wait = true)
    }
}

fun main(args: Array<String>) = ServerMain().main(args)
