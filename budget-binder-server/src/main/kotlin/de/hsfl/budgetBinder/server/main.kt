package de.hsfl.budgetBinder.server

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import de.hsfl.budgetBinder.server.config.Config
import de.hsfl.budgetBinder.server.config.getServerConfig
import io.ktor.network.tls.certificates.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.File
import java.security.KeyStore

class ServerMain : CliktCommand() {
    private val configFile: File? by option("--configFile", "-conf").file(
        mustExist = true,
        canBeDir = false,
        mustBeReadable = true
    )

    override fun run(): Unit = runBlocking {
        val config = getServerConfig(configFile = configFile)

        val keyStore = when (config.server.sslState) {
            Config.SSLState.DEV -> generateCertificate(
                file = File("data/dev_keystore.jks"),
                keyAlias = "Budget Binder Server",
                keyPassword = config.server.keyStorePassword,
                jksPassword = config.server.keyStorePassword
            )
            Config.SSLState.SSL -> withContext(Dispatchers.IO) {
                KeyStore.getInstance(File(config.server.keyStorePath), config.server.keyStorePassword.toCharArray())
            }
            Config.SSLState.NONE -> null
        }

        val environment = applicationEngineEnvironment {
            module {
                mainModule(config)
            }
            log = LoggerFactory.getLogger("ktor.application")

            connector {
                host = config.server.host
                port = config.server.port
            }
            if (config.server.sslState > Config.SSLState.NONE) {
                sslConnector(
                    keyStore = keyStore!!,
                    keyAlias = "Budget Binder Server",
                    keyStorePassword = { config.server.keyStorePassword.toCharArray() },
                    privateKeyPassword = { config.server.keyStorePassword.toCharArray() }
                ) {
                    host = config.server.sslHost
                    port = config.server.sslPort
                }
            }
            if (config.server.sslState == Config.SSLState.DEV) {
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
