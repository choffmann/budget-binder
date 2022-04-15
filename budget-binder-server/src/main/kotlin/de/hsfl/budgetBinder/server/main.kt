package de.hsfl.budgetBinder.server

import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    embeddedServer(Netty, port = 8080, module = Application::mainModule, watchPaths = listOf("classes", "resources")).start(wait = true)
}