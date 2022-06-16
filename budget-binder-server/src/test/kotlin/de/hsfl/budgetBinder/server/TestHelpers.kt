package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.server.config.getServerConfig
import io.ktor.client.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun customTestApplication(block: suspend ApplicationTestBuilder.(client: HttpClient) -> Unit) {
    testApplication {
        application {
            val configString = """
            dataBase:
                dbType: SQLITE
                sqlitePath: file:test?mode=memory&cache=shared
            jwt:
                accessSecret: testSecret
                refreshSecret: testSecret2
                accessMinutes: 10
            """.trimIndent()
            mainModule(getServerConfig(configString = configString))
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        block(client)
    }
}

fun customTestApplicationWithLogin(block: suspend ApplicationTestBuilder.(client: HttpClient) -> Unit) =
    customTestApplication { client ->
        loginUser(client)
        block(client)
    }

inline fun <reified T> wrapSuccess(value: T): APIResponse<T> {
    return APIResponse(data = value, success = true)
}

inline fun <reified T> wrapFailure(message: String, code: Int = 200): APIResponse<T> {
    return APIResponse(error = ErrorModel(message = message, code), success = false)
}

fun formatToPeriod(time: LocalDateTime): String {
    return time.format(DateTimeFormatter.ofPattern("MM-yyyy"))
}
