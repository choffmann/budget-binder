package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.AuthToken
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.server.models.CategoryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.HttpCookie
import kotlin.test.*

class ApplicationTest {
    @BeforeTest
    fun registerTestUser() {
        withCustomTestApplication(Application::mainModule) {
            registerUser()
        }
    }

    @AfterTest
    fun deleteTestUser() {
        withCustomTestApplication(Application::mainModule) {
            transaction {
                UserEntity.all().forEach {
                    CategoryEntity[it.category!!].delete()
                    it.delete()
                }
            }
        }
    }

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
    fun testRegisterLoginAndLogout() {
        withCustomTestApplication(Application::mainModule) {
            cookiesSession {

                handleRequest(HttpMethod.Get, "/login").apply {
                    assertEquals(HttpStatusCode.NotFound, response.status())
                    assertNull(response.content)
                }

                handleRequest(HttpMethod.Post, "/login").apply {
                    assertEquals(HttpStatusCode.Unauthorized, response.status())
                    assertNotNull(response.content)
                    val response: APIResponse<AuthToken> = decodeFromString(response.content!!)
                    val shouldResponse: APIResponse<AuthToken> = wrapFailure("Unauthorized")
                    assertEquals(shouldResponse, response)
                }

                with(handleRequest(HttpMethod.Post, "/login") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(
                        listOf(
                            "username" to "falseTest@test.com",
                            "password" to "falsetest"
                        ).formUrlEncode()
                    )
                }) {
                    assertEquals(HttpStatusCode.Unauthorized, response.status())
                    assertNotNull(response.content)
                    val response: APIResponse<AuthToken> = decodeFromString(response.content!!)
                    val shouldResponse: APIResponse<AuthToken> = wrapFailure("Unauthorized")
                    assertEquals(shouldResponse, response)
                }

                loginUser {
                    val setCookieHeader = response.headers[HttpHeaders.SetCookie]
                    assertNotNull(setCookieHeader)
                    val cookie = HttpCookie.parse(setCookieHeader)
                    assertNotNull(cookie)
                    assertEquals(1, cookie.size)
                    assertEquals("jwt", cookie[0].name)
                    assertNotEquals("", cookie[0].value)
                }

                handleRequest(HttpMethod.Get, "/me").apply {
                    assertEquals(HttpStatusCode.Unauthorized, response.status())
                    assertNotNull(response.content)
                    val response: APIResponse<User> = decodeFromString(response.content!!)
                    val shouldResponse: APIResponse<User> = wrapFailure("Unauthorized")
                    assertEquals(shouldResponse, response)
                }

                with(handleRequest(HttpMethod.Get, "/me") {
                    val bearer =
                        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyaWQiOjF9.OsLv52jTx-f-vcuQEJ6FJ-kTJ_DYm3XqVpjLwagQtM0"
                    addHeader(HttpHeaders.Authorization, "Bearer $bearer")
                }) {
                    assertEquals(HttpStatusCode.Unauthorized, response.status())
                    assertNotNull(response.content)
                    val response: APIResponse<User> = decodeFromString(response.content!!)
                    val shouldResponse: APIResponse<User> = wrapFailure("Unauthorized")
                    assertEquals(shouldResponse, response)
                }

                checkMeSuccess()

                handleRequest(HttpMethod.Get, "/refresh_token").apply {
                    assertEquals(HttpStatusCode.OK, response.status())
                    assertNotNull(response.content)
                    val token: APIResponse<AuthToken> = decodeFromString(response.content!!)
                    assert(token.success)
                    assertNotNull(token.data)
                }

                handleRequest(HttpMethod.Get, "/logout").apply {
                    assertEquals(HttpStatusCode.Unauthorized, response.status())
                    assertNotNull(response.content)
                    val response: APIResponse<AuthToken> = decodeFromString(response.content!!)
                    val shouldResponse: APIResponse<AuthToken> = wrapFailure("Unauthorized")
                    assertEquals(shouldResponse, response)
                }

                sendAuthenticatedRequest(HttpMethod.Get, "/logout") {
                    assertEquals(HttpStatusCode.OK, response.status())
                    assertNotNull(response.content)
                    val logoutResponse: APIResponse<AuthToken> = decodeFromString(response.content!!)
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

                checkMeSuccess()

                TestUser.accessToken = null

                checkMeFailure()
            }

            handleRequest(HttpMethod.Get, "/refresh_token").apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
                assertNotNull(response.content)
                val token: APIResponse<AuthToken> = decodeFromString(response.content!!)
                val shouldToken: APIResponse<AuthToken> = wrapFailure("No Refresh Cookie")
                assertEquals(shouldToken, token)
            }
        }
    }

    @Test
    fun testLogoutAll() {
        withCustomTestApplication(Application::mainModule) {
            cookiesSession {
                loginUser()
                checkMeSuccess()

                val tokenVersion = transaction {
                    val tokenVersion = UserEntity.all().first().tokenVersion
                    assertEquals(1, tokenVersion)
                    tokenVersion
                }

                sendAuthenticatedRequest(HttpMethod.Get, "/logout?all=true") {
                    assertEquals(HttpStatusCode.OK, response.status())
                    assertNotNull(response.content)
                    val response: APIResponse<AuthToken> = decodeFromString(response.content!!)
                    val shouldResponse = wrapSuccess(AuthToken(""))
                    assertEquals(shouldResponse, response)
                }

                transaction {
                    val newTokenVersion = UserEntity.all().first().tokenVersion
                    assertNotEquals(tokenVersion, newTokenVersion)
                }

                checkMeFailure()
            }
        }
    }

    @Test
    fun testUserEndpoints() {
        withCustomTestApplication(Application::mainModule) {
            loginUser()
            checkMeSuccess()

            val userId = transaction { UserEntity.all().first().id.value }

            sendAuthenticatedRequest(HttpMethod.Patch, "/me") {
                assertEquals(HttpStatusCode.OK, response.status())

                val user: APIResponse<User> = decodeFromString(response.content!!)
                val shouldUser: APIResponse<User> = wrapFailure("not the right Parameters provided")
                assertEquals(shouldUser, user)
            }

            val patchedUser = User.Put("changedTest", "changedSurname", "newPassword")

            with(handleRequest(HttpMethod.Patch, "/me") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(toJsonString(patchedUser))
            }) {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
                assertNotNull(response.content)
                val response: APIResponse<User> = decodeFromString(response.content!!)
                val shouldResponse: APIResponse<User> = wrapFailure("Unauthorized")
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(HttpMethod.Patch, "/me", toJsonString(patchedUser)) {
                assertEquals(HttpStatusCode.OK, response.status())

                val user: APIResponse<User> = decodeFromString(response.content!!)
                val shouldUser = wrapSuccess(User(userId, "changedTest", "changedSurname", TestUser.email))
                assertEquals(shouldUser, user)

                transaction {
                    val userEntity = UserEntity[userId]
                    assertEquals("changedTest", userEntity.firstName)
                    assertEquals("changedSurname", userEntity.name)
                    assertEquals("changedSurname", userEntity.name)
                }
            }

            with(handleRequest(HttpMethod.Post, "/login") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(
                    listOf(
                        "username" to TestUser.email,
                        "password" to TestUser.password
                    ).formUrlEncode()
                )
            }) {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
                assertNotNull(response.content)
                val response: APIResponse<User> = decodeFromString(response.content!!)
                val shouldResponse: APIResponse<User> = wrapFailure("Unauthorized")
                assertEquals(shouldResponse, response)
            }

            with(handleRequest(HttpMethod.Post, "/login") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(
                    listOf(
                        "username" to TestUser.email,
                        "password" to "newPassword"
                    ).formUrlEncode()
                )
            }) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val token: APIResponse<AuthToken> = decodeFromString(response.content!!)
                assert(token.success)
                assertNotNull(token.data)
                TestUser.accessToken = token.data!!.token
            }

            handleRequest(HttpMethod.Delete, "/me").apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
                assertNotNull(response.content)
                val response: APIResponse<User> = decodeFromString(response.content!!)
                val shouldResponse: APIResponse<User> = wrapFailure("Unauthorized")
                assertEquals(shouldResponse, response)
            }

            sendAuthenticatedRequest(HttpMethod.Delete, "/me") {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val user: APIResponse<User> = decodeFromString(response.content!!)

                val shouldUser = wrapSuccess(User(userId, "changedTest", "changedSurname", TestUser.email))
                assertEquals(shouldUser, user)

                transaction {
                    assertNull(UserEntity.findById(userId))
                }
            }

            with(handleRequest(HttpMethod.Get, "/me") {
                addHeader(HttpHeaders.Authorization, "Bearer ${TestUser.accessToken ?: ""}")
            }) {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
                assertNotNull(response.content)
                val response: APIResponse<User> = decodeFromString(response.content!!)
                val shouldResponse: APIResponse<User> = wrapFailure("Unauthorized")
                assertEquals(shouldResponse, response)
            }

            with(handleRequest(HttpMethod.Post, "/login") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                setBody(
                    listOf(
                        "username" to TestUser.email,
                        "password" to "newPassword"
                    ).formUrlEncode()
                )
            }) {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
                assertNotNull(response.content)
                val response: APIResponse<AuthToken> = decodeFromString(response.content!!)
                val shouldResponse: APIResponse<AuthToken> = wrapFailure("Unauthorized")
                assertEquals(shouldResponse, response)
            }
        }
    }
}
