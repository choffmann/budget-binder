package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.server.config.Config
import de.hsfl.budgetBinder.server.config.getServerConfig
import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun <R> withCustomTestApplication(
    moduleFunction: Application.(config: Config) -> Unit,
    test: TestApplicationEngine.() -> R
) {
    withApplication(createTestEnvironment()) {
        val configString = """
            dataBase:
                dbType: SQLITE
                sqlitePath: file:test?mode=memory&cache=shared
            server:
                frontendAddresses: http://localhost:8081
            jwt:
                accessSecret: testSecret
                refreshSecret: testSecret2
                accessMinutes: 10
            """.trimIndent()
        moduleFunction(application, getServerConfig(configString = configString))
        test()
    }
}

inline fun <reified T> toJsonString(value: T): String {
    return Json.encodeToString(serializer(), value)
}

inline fun <reified T> decodeFromString(value: String): APIResponse<T> {
    return Json.decodeFromString(APIResponse.serializer(serializer()), value)
}

inline fun <reified T> wrapSuccess(value: T): APIResponse<T> {
    return APIResponse(data = value, success = true)
}

inline fun <reified T> wrapFailure(message: String): APIResponse<T> {
    return APIResponse(error = ErrorModel(message = message), success = false)
}

fun formatToPeriod(time: LocalDateTime): String {
    return time.format(DateTimeFormatter.ofPattern("MM-yyyy"))
}
