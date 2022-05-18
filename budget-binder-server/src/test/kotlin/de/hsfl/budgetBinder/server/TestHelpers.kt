package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.AuthToken
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.server.config.Config
import de.hsfl.budgetBinder.server.config.getServerConfig
import de.hsfl.budgetBinder.server.models.UserEntity
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

object TestUser {
    const val email = "test@test.com"
    const val password = "test-test"
    const val firstName = "test"
    const val surName = "Test"

    val userIn = User.In(firstName, surName, email, password)

    fun getTestUser(id: Int): User {
        return User(id, firstName, surName, email)
    }

    var accessToken: String? = null
}

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

fun TestApplicationEngine.loginUser(block: TestApplicationCall.() -> Unit = {}) {
    with(handleRequest(HttpMethod.Post, "/login") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        setBody(
            listOf(
                "username" to TestUser.email,
                "password" to TestUser.password
            ).formUrlEncode()
        )
    }) {
        assertEquals(HttpStatusCode.OK, response.status())
        assertNotNull(response.content)
        val token: APIResponse<AuthToken> = decodeFromString(response.content!!)
        assert(token.success)
        assertNotNull(token.data)
        TestUser.accessToken = token.data!!.token
        block()
    }
}

fun TestApplicationEngine.sendAuthenticatedRequest(
    method: HttpMethod,
    path: String,
    body: String? = null,
    block: TestApplicationCall.() -> Unit
) {
    with(handleRequest(method, path) {
        addHeader(HttpHeaders.Authorization, "Bearer ${TestUser.accessToken ?: ""}")
        body?.let {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(it)
        }
    }, block)
}

fun TestApplicationEngine.checkMeSuccess() {
    sendAuthenticatedRequest(HttpMethod.Get, "/me") {
        assertEquals(HttpStatusCode.OK, response.status())
        assertNotNull(response.content)
        val response: APIResponse<User> = decodeFromString(response.content!!)

        val id = transaction { UserEntity.all().first().id.value }

        val shouldUser = wrapSuccess(TestUser.getTestUser(id))
        assertEquals(response, shouldUser)
    }
}

fun TestApplicationEngine.checkMeFailure() {
    sendAuthenticatedRequest(HttpMethod.Get, "/me") {
        assertEquals(HttpStatusCode.Unauthorized, response.status())
        assertNull(response.content)
    }
}
