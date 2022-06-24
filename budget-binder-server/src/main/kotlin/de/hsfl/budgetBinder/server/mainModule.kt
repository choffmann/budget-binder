package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.server.config.Config
import de.hsfl.budgetBinder.server.plugins.*
import de.hsfl.budgetBinder.server.routes.*
import io.ktor.server.application.*

fun Application.mainModule(config: Config) {

    // install all Plugin Modules
    configureDI(config)
    configureDatabase()
    configureLogging()
    configureForwardHeaders()
    configureAuth()
    configureContentNegotiation()
    configureStatusPages()

    // install all Routing Modules
    baseRoutes()
    userRoutes()
    authRoutes()
    categoryRoutes()
    entryRoutes()
}
