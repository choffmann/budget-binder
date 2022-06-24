package de.hsfl.budgetBinder.server.plugins

import de.hsfl.budgetBinder.server.config.Config
import io.ktor.server.application.*
import io.ktor.server.plugins.forwardedheaders.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.configureForwardHeaders() {
    val config: Config by closestDI().instance()

    if (config.server.forwardedHeaderSupport)
        install(XForwardedHeaders)
}
