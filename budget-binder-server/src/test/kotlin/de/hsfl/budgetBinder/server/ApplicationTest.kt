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
            with(handleRequest(HttpMethod.Post, "/register") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(toJsonString(TestUser.userIn))
            }) {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                val user: APIResponse<User> = decodeFromString(response.content!!)
                val id = transaction {
                    val userEntity = UserEntity.all().first()
                    assertEquals(userEntity.email, TestUser.email)
                    assertNotNull(userEntity.category)
                    val categoryEntity = CategoryEntity[userEntity.category!!]
                    assertNotNull(categoryEntity)
                    assertEquals(categoryEntity.name, "default")
                    userEntity.id.value
                }
                val shouldUser = wrapSuccessFull(
                    User(
                        id,
                        TestUser.firstName,
                        TestUser.surName,
                        TestUser.email
                    )
                )
                assertEquals(user, shouldUser)

            }
        }
    }

    @AfterTest
    fun deleteTestUser() {
        withCustomTestApplication(Application::mainModule) {
            transaction {
                UserEntity.all().forEach { it.delete() }
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
                    assertNull(response.content)
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
                    assertNull(response.content)
                }

                loginUser {
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
            cookiesSession {
                loginUser()
                checkMeSuccess()

                val tokenVersion = transaction {
                    val tokenVersion = UserEntity.all().first().tokenVersion
                    assertEquals(tokenVersion, 1)
                    tokenVersion
                }

                sendAuthenticatedRequest(HttpMethod.Get, "/logout?all=true") {
                    assertEquals(HttpStatusCode.OK, response.status())
                    assertNull(response.content)
                }

                transaction {
                    val newTokenVersion = UserEntity.all().first().tokenVersion
                    assertNotEquals(newTokenVersion, tokenVersion)
                }

                checkMeFailure()
            }
        }
    }

    @Test
    fun test() {

    }
}
