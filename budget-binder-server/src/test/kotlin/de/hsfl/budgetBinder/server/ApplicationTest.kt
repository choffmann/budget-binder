package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.server.config.Config
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
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
                assertEquals("{}", response.content)
            }
            with(handleRequest(HttpMethod.Post, "/register") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())

                val jsonEncoded = Json.encodeToString(User.In.serializer(), User.In("test", "test", "example@jetbrains.com", "foobar"))
                setBody(jsonEncoded)

            }) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val user: APIResponse<User> = Json.decodeFromString(APIResponse.serializer(User.serializer()), response.content!!)
                val shouldUser = APIResponse(data = User(1, "test", "test", "example@jetbrains.com"), success = true)
                assertEquals(user, shouldUser)
            }
        }
    }
}