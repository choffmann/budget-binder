package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.AuthToken
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.server.models.CategoryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import io.ktor.http.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull


fun TestApplicationEngine.registerUser() {
    with(handleRequest(HttpMethod.Post, "/register") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        setBody(toJsonString(TestUser.userIn))
    }) {
        assertEquals(HttpStatusCode.OK, response.status())
        assertNotNull(response.content)
        val user: APIResponse<User> = decodeFromString(response.content!!)
        val id = transaction {
            val userEntity = UserEntity.all().first()
            assertEquals(TestUser.email, userEntity.email)
            assertNotNull(userEntity.category)
            val categoryEntity = CategoryEntity[userEntity.category!!]
            assertNotNull(categoryEntity)
            assertEquals("default", categoryEntity.name)
            userEntity.id.value
        }
        val shouldUser = wrapSuccess(
            User(
                id,
                TestUser.firstName,
                TestUser.surName,
                TestUser.email
            )
        )
        assertEquals(shouldUser, user)
    }
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
        assertEquals(shouldUser, response)
    }
}

fun TestApplicationEngine.checkMeFailure() {
    sendAuthenticatedRequest(HttpMethod.Get, "/me") {
        assertEquals(HttpStatusCode.Unauthorized, response.status())
        assertNull(response.content)
    }
}
