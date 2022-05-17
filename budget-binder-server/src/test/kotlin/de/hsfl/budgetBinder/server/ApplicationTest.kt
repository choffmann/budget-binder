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
                setBody(toJsonString(TestUser.userIn))
            }) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val user: APIResponse<User> = decodeFromString(response.content!!)
                val shouldUser = wrapSuccessFull(TestUser.user)
                assertEquals(user, shouldUser)

                transaction {
                    val userEntity = UserEntity[TestUser.id]
                    assertEquals(userEntity.email, TestUser.email)
                    assertNotNull(userEntity.category)
                    val categoryEntity = CategoryEntity[userEntity.category!!]
                    assertNotNull(categoryEntity)
                    assertEquals(categoryEntity.name, "default")
                }
            }
        }
    }

    @Test
    fun testLoginAndLogout() {
        withCustomTestApplication(Application::mainModule) {
            registerUser()
            cookiesSession {
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

                    val setCookieHeader = response.headers[HttpHeaders.SetCookie]
                    assertNotNull(setCookieHeader)
                    val cookie = HttpCookie.parse(setCookieHeader)
                    assertNotNull(cookie)
                    assertEquals(cookie.size, 1)
                    assertEquals(cookie[0].name, "jwt")
                    assertNotEquals(cookie[0].value, "")
                }

                handleRequest(HttpMethod.Get, "/me").apply {
                    assertEquals(HttpStatusCode.Unauthorized, response.status())
                    assertNull(response.content)
                }

                with(handleRequest(HttpMethod.Get, "/me") {
                    val bearer =
                        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyaWQiOjF9.OsLv52jTx-f-vcuQEJ6FJ-kTJ_DYm3XqVpjLwagQtM0"
                    addHeader(HttpHeaders.Authorization, "Bearer $bearer")
                }) {
                    assertEquals(HttpStatusCode.Unauthorized, response.status())
                    assertNull(response.content)
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
                    assertNull(response.content)
                }

                sendAuthenticatedRequest(HttpMethod.Get, "/logout") {
                    assertEquals(HttpStatusCode.OK, response.status())
                    assertNull(response.content)

                    val setCookieHeader = response.headers[HttpHeaders.SetCookie]
                    assertNotNull(setCookieHeader)
                    val cookie = HttpCookie.parse(setCookieHeader)
                    assertNotNull(cookie)
                    assertEquals(cookie.size, 1)
                    assertEquals(cookie[0].name, "jwt")
                    assertEquals(cookie[0].value, "")
                }

                checkMeSuccess()

                TestUser.accessToken = null

                checkMeFailure()
            }

            handleRequest(HttpMethod.Get, "/refresh_token").apply {
                assertEquals(HttpStatusCode.Unauthorized, response.status())
                assertNotNull(response.content)
                val token: APIResponse<AuthToken> = decodeFromString(response.content!!)
                assert(!token.success)
                assertEquals(token.error!!.message, "No Refresh Cookie")
            }
        }
    }

    @Test
    fun testLogoutAll() {
        withCustomTestApplication(Application::mainModule) {
            registerUser()

            cookiesSession {
                loginUser()
                checkMeSuccess()

                val tokenVersion = transaction {
                    val tokenVersion = UserEntity[TestUser.id].tokenVersion
                    assertEquals(tokenVersion, 1)
                    tokenVersion
                }

                sendAuthenticatedRequest(HttpMethod.Get, "/logout?all=true") {
                    assertEquals(HttpStatusCode.OK, response.status())
                    assertNull(response.content)
                }

                transaction {
                    val newTokenVersion = UserEntity[TestUser.id].tokenVersion
                    assertNotEquals(newTokenVersion, tokenVersion)
                }

                checkMeFailure()
            }
        }
    }
}
