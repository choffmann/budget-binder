package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.AuthToken
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.server.models.CategoryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.HttpCookie
import kotlin.test.*

class ApplicationTest {
    @BeforeTest
    fun registerTestUser() = customTestApplication { client ->
        registerUser(client)
    }

    @AfterTest
    fun deleteTestUser() = transaction {
        UserEntity.all().forEach {
            CategoryEntity[it.category!!].delete()
            it.delete()
        }
    }


    @Test
    fun testRoot() = customTestApplication { client ->
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("{Accept=[application/json], Accept-Charset=[UTF-8], Content-Length=[0]}", response.bodyAsText())
    }


    @Test
    fun testRegisterLoginAndLogout() = customTestApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(HttpCookies)
        }

        client.get("/login").let { response ->
            assertEquals(HttpStatusCode.MethodNotAllowed, response.status)
            val responseBody: APIResponse<AuthToken> = response.body()
            val shouldResponse: APIResponse<AuthToken> = wrapFailure("Method Not Allowed")
            assertEquals(shouldResponse, responseBody)
        }

        client.post("/login").let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<AuthToken> = response.body()
            val shouldResponse: APIResponse<AuthToken> = wrapFailure("Unauthorized")
            assertEquals(shouldResponse, responseBody)
        }

        client.post("/login") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "username" to "falseTest@test.com",
                    "password" to "falsetest"
                ).formUrlEncode()
            )
        }.let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<AuthToken> = response.body()
            val shouldResponse: APIResponse<AuthToken> = wrapFailure("Unauthorized")
            assertEquals(shouldResponse, responseBody)
        }

        loginUser(client) { response ->
            val setCookieHeader = response.headers[HttpHeaders.SetCookie]
            assertNotNull(setCookieHeader)
            val cookie = HttpCookie.parse(setCookieHeader)
            assertNotNull(cookie)
            assertEquals(1, cookie.size)
            assertEquals("jwt", cookie[0].name)
            assertNotEquals("", cookie[0].value)
        }

        client.get("/me").let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<User> = response.body()
            val shouldResponse: APIResponse<User> = wrapFailure("Unauthorized")
            assertEquals(shouldResponse, responseBody)
        }

        client.get("/me") {
            val bearer =
                "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyaWQiOjF9.OsLv52jTx-f-vcuQEJ6FJ-kTJ_DYm3XqVpjLwagQtM0"
            header(HttpHeaders.Authorization, "Bearer $bearer")
        }.let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<User> = response.body()
            val shouldResponse: APIResponse<User> = wrapFailure("Unauthorized")
            assertEquals(shouldResponse, responseBody)
        }

        checkMeSuccess(client)

        client.get("/refresh_token").let { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<AuthToken> = response.body()
            assert(responseBody.success)
            assertNotNull(responseBody.data)
        }

        client.get("/logout").let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<AuthToken> = response.body()
            val shouldResponse: APIResponse<AuthToken> = wrapFailure("Unauthorized")
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Get, "/logout") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val logoutResponse: APIResponse<AuthToken> = response.body()
            val shouldResponse = wrapSuccess(AuthToken(""))
            assertEquals(shouldResponse, logoutResponse)

            val setCookieHeader = response.headers[HttpHeaders.SetCookie]
            assertNotNull(setCookieHeader)
            val cookie = HttpCookie.parse(setCookieHeader)
            assertNotNull(cookie)
            assertEquals(1, cookie.size)
            assertEquals("jwt", cookie[0].name)
            assertEquals("", cookie[0].value)
        }

        checkMeSuccess(client)
        TestUser.accessToken = null
        checkMeFailure(client)
    }


    @Test
    fun testRefreshTokenWithoutCookie() = customTestApplication { client ->
        val response = client.get("/refresh_token")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        val responseBody: APIResponse<AuthToken> = response.body()
        val shouldResponse: APIResponse<AuthToken> = wrapFailure("False Refresh Cookie")
        assertEquals(shouldResponse, responseBody)
    }

    @Test
    fun testLogoutAll() = customTestApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(HttpCookies)
        }
        loginUser(client)
        checkMeSuccess(client)

        val tokenVersion = transaction {
            val tokenVersion = UserEntity.all().first().tokenVersion
            assertEquals(1, tokenVersion)
            tokenVersion
        }

        sendAuthenticatedRequest(client, HttpMethod.Get, "/logout?all=true") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<AuthToken> = response.body()
            val shouldResponse = wrapSuccess(AuthToken(""))
            assertEquals(shouldResponse, responseBody)
        }

        transaction {
            val newTokenVersion = UserEntity.all().first().tokenVersion
            assertNotEquals(tokenVersion, newTokenVersion)
        }

        checkMeFailure(client)

        client.get("/refresh_token").let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<AuthToken> = response.body()
            val shouldResponse: APIResponse<AuthToken> = wrapFailure("False Refresh Cookie")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testUserEndpoints() = customTestApplicationWithLogin { client ->
        checkMeSuccess(client)

        val userId = transaction { UserEntity.all().first().id.value }

        sendAuthenticatedRequest(client, HttpMethod.Patch, "/me") { response ->
            assertEquals(HttpStatusCode.OK, response.status)

            val user: APIResponse<User> = response.body()
            val shouldUser: APIResponse<User> = wrapFailure("not the right Parameters provided")
            assertEquals(shouldUser, user)
        }

        val patchedUser = User.Put("changedTest", "changedSurname", "newPassword")

        client.patch("/me") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(patchedUser)
        }.let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<User> = response.body()
            val shouldResponse: APIResponse<User> = wrapFailure("Unauthorized")
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequestWithBody(client, HttpMethod.Patch, "/me", patchedUser) { response ->
            assertEquals(HttpStatusCode.OK, response.status)

            val user: APIResponse<User> = response.body()
            val shouldUser = wrapSuccess(User(userId, "changedTest", "changedSurname", TestUser.email))
            assertEquals(shouldUser, user)

            transaction {
                val userEntity = UserEntity[userId]
                assertEquals("changedTest", userEntity.firstName)
                assertEquals("changedSurname", userEntity.name)
                assertEquals("changedSurname", userEntity.name)
            }
        }

        client.post("/login") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "username" to TestUser.email,
                    "password" to TestUser.password
                ).formUrlEncode()
            )
        }.let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<User> = response.body()
            val shouldResponse: APIResponse<User> = wrapFailure("Unauthorized")
            assertEquals(shouldResponse, responseBody)
        }

        client.post("/login") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "username" to TestUser.email,
                    "password" to "newPassword"
                ).formUrlEncode()
            )
        }.let { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val token: APIResponse<AuthToken> = response.body()
            assert(token.success)
            assertNotNull(token.data)
            TestUser.accessToken = token.data!!.token
        }

        client.delete("/me").let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<User> = response.body()
            val shouldResponse: APIResponse<User> = wrapFailure("Unauthorized")
            assertEquals(shouldResponse, responseBody)
        }

        sendAuthenticatedRequest(client, HttpMethod.Delete, "/me") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val user: APIResponse<User> = response.body()

            val shouldUser = wrapSuccess(User(userId, "changedTest", "changedSurname", TestUser.email))
            assertEquals(shouldUser, user)

            transaction {
                assertNull(UserEntity.findById(userId))
            }
        }

        client.get("/me") {
            header(HttpHeaders.Authorization, "Bearer ${TestUser.accessToken ?: ""}")
        }.let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<User> = response.body()
            val shouldResponse: APIResponse<User> = wrapFailure("Unauthorized")
            assertEquals(shouldResponse, responseBody)
        }

        client.post("/login") {
            header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "username" to TestUser.email,
                    "password" to "newPassword"
                ).formUrlEncode()
            )
        }.let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<AuthToken> = response.body()
            val shouldResponse: APIResponse<AuthToken> = wrapFailure("Unauthorized")
            assertEquals(shouldResponse, responseBody)
        }
    }
}
