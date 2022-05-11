package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.server.config.Config
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

fun <R> withCustomTestApplication(
    moduleFunction: Application.(config: Config?, confString: String?) -> Unit,
    test: TestApplicationEngine.() -> R
) {
    withApplication(createTestEnvironment()) {
        val stringConfig = """
            dataBase:
                dbType: SQLITE
                sqlitePath: file:test?mode=memory&cache=shared
            server:
                frontendAddresses: http://localhost:8081
            jwt:
                accessSecret: testSecret
                refreshSecret: testSecret2
                accessMinutes: 1
            """.trimIndent()
        moduleFunction(application, null, stringConfig)
        test()
    }
}

class ApplicationTest {
    @Test
    fun testRoot() {
        withCustomTestApplication(Application::mainModule) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Hello, world!", response.content)
            }
        }
    }
}