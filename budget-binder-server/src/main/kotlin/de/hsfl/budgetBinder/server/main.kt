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

        val keyStore = when {
            config.server.dev && config.server.ssl -> {
                generateCertificate(
                    file = File(config.server.keyStorePath),
                    keyAlias = "Budget Binder Server",
                    keyPassword = config.server.keyStorePassword,
                    jksPassword = config.server.keyStorePassword
                )
            }
            config.server.ssl -> {
                withContext(Dispatchers.IO) {
                    KeyStore.getInstance(File(config.server.keyStorePath), config.server.keyStorePassword.toCharArray())
                }
            }
            else -> null
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
            if (keyStore != null) {
                sslConnector(
                    keyStore = keyStore,
                    keyAlias = "Budget Binder Server",
                    keyStorePassword = { config.server.keyStorePassword.toCharArray() },
                    privateKeyPassword = { config.server.keyStorePassword.toCharArray() }
                ) {
                    host = config.server.sslHost
                    port = config.server.sslPort
                }
            }
            if (config.server.dev) {
                watchPaths = listOf("build/classes", "build/resources")
            }
        }

        embeddedServer(Netty, environment = environment).start(wait = true)
    }
}

fun main(args: Array<String>) = ServerMain().main(args)
