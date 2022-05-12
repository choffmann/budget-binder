package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.server.config.Config
import de.hsfl.budgetBinder.server.config.getServerConfig
import de.hsfl.budgetBinder.server.models.CategoryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

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
                accessMinutes: 1
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

inline fun <reified T> wrapSuccessFull(value: T): APIResponse<T> {
    return APIResponse(data = value, success = true)
}

class ApplicationTest {
    @Test
    fun testRoot() {
        withCustomTestApplication(Application::mainModule) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("{}", response.content)
            }
        }
    }
    @Test
    fun testRegister() {
        withCustomTestApplication(Application::mainModule) {
            with(handleRequest(HttpMethod.Post, "/register") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(toJsonString(User.In("test", "test", "example@jetbrains.com", "foobar")))

            }) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val user: APIResponse<User> = decodeFromString(response.content!!)
                val shouldUser = wrapSuccessFull(User(1, "test", "test", "example@jetbrains.com"))
                assertEquals(user, shouldUser)

                transaction {
                    val userEntity = UserEntity[1]
                    assertEquals(userEntity.email, "example@jetbrains.com")
                    assertNotNull(userEntity.category)
                    val categoryEntity = CategoryEntity[userEntity.category!!]
                    assertNotNull(categoryEntity)
                    assertEquals(categoryEntity.name, "default")
                }
            }
        }
    }
}