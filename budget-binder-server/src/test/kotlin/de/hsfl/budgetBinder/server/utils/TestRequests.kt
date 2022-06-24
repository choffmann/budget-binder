package de.hsfl.budgetBinder.server.utils

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.AuthToken
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.server.models.UserEntity
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

suspend fun HttpClient.registerUser() {
    val response = this.post("/register") {
        contentType(ContentType.Application.Json)
        setBody(TestUser.userIn)
    }
    assertEquals(HttpStatusCode.OK, response.status)
    val responseBody: APIResponse<User> = response.body()
    val id = transaction {
        val userEntity = UserEntity.all().first()
        assertEquals(TestUser.email, userEntity.email)
        userEntity.id.value
    }
    val shouldResponse = wrapSuccess(
        User(
            id,
            TestUser.firstName,
            TestUser.surName,
            TestUser.email
        )
    )
    assertEquals(shouldResponse, responseBody)
}

suspend fun HttpClient.loginUser(block: (response: HttpResponse) -> Unit = {}) {
    val response = this.post("/login") {
        contentType(ContentType.Application.FormUrlEncoded)
        setBody(
            listOf(
                "username" to TestUser.email,
                "password" to TestUser.password
            ).formUrlEncode()
        )
    }
    assertEquals(HttpStatusCode.OK, response.status)
    val responseBody: APIResponse<AuthToken> = response.body()
    assert(responseBody.success)
    assertNotNull(responseBody.data)
    TestUser.accessToken = responseBody.data!!.token
    block(response)
}

suspend inline fun HttpClient.sendAuthenticatedRequest(
    sendMethod: HttpMethod,
    path: String,
    block: (response: HttpResponse) -> Unit
) {
    block(
        this.request(path) {
            method = sendMethod
            header(HttpHeaders.Authorization, "Bearer ${TestUser.accessToken ?: ""}")
        }
    )
}

suspend inline fun <reified T> HttpClient.sendAuthenticatedRequestWithBody(
    sendMethod: HttpMethod,
    path: String,
    body: T,
    block: (response: HttpResponse) -> Unit
) {
    block(
        this.request(path) {
            method = sendMethod
            header(HttpHeaders.Authorization, "Bearer ${TestUser.accessToken ?: ""}")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(body)
        }
    )
}

suspend fun HttpClient.checkMeSuccess() {
    this.sendAuthenticatedRequest(HttpMethod.Get, "/me") { response ->
        assertEquals(HttpStatusCode.OK, response.status)
        val responseBody: APIResponse<User> = response.body()

        val id = transaction { UserEntity.all().first().id.value }

        val shouldUser = wrapSuccess(TestUser.getTestUser(id))
        assertEquals(shouldUser, responseBody)
    }
}

suspend fun HttpClient.checkMeFailure() {
    this.sendAuthenticatedRequest(HttpMethod.Get, "/me") { response ->
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        val responseBody: APIResponse<User> = response.body()
        val shouldResponse: APIResponse<User> = wrapFailure("Your accessToken is absent or does not match.", 401)
        assertEquals(shouldResponse, responseBody)
    }
}
