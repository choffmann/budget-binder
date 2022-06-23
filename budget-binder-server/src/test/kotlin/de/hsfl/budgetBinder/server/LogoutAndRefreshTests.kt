package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.AuthToken
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.utils.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.HttpCookie
import kotlin.test.*

class LogoutAndRefreshTests {

    private suspend fun HttpClient.logoutUser(all: Boolean) {
        this.sendAuthenticatedRequest(HttpMethod.Get, "/logout?all=$all") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<AuthToken> = response.body()
            val shouldResponse: APIResponse<AuthToken> = wrapSuccess(AuthToken(""))
            assertEquals(shouldResponse, responseBody)

            val setCookieHeader = response.headers[HttpHeaders.SetCookie]
            assertNotNull(setCookieHeader)
            val cookie = HttpCookie.parse(setCookieHeader)
            assertNotNull(cookie)
            assertEquals(1, cookie.size)
            assertEquals("jwt", cookie[0].name)
            assertEquals("", cookie[0].value)
        }
    }

    @BeforeTest
    fun registerTestUser() = customTestApplication { client ->
        client.registerUser()
    }

    @AfterTest
    fun deleteTestUser() = transaction {
        UserEntity.all().forEach { it.delete() }
    }

    @Test
    fun testRefreshTokenUnauthorized() = customTestApplication { client ->
        client.get("refresh_token").let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<AuthToken> = response.body()
            val shouldResponse: APIResponse<AuthToken> = wrapFailure("Your refreshToken is absent.", 401)
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testRefreshToken() = customTestApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(HttpCookies) {
                storage = CustomCookieStorage()
            }
        }

        client.loginUser()

        client.get("refresh_token").let { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<AuthToken> = response.body()
            assert(responseBody.success)
            assertNull(responseBody.error)
            assertNotNull(responseBody.data)

            val setCookieHeader = response.headers[HttpHeaders.SetCookie]
            assertNotNull(setCookieHeader)
            val cookie = HttpCookie.parse(setCookieHeader)
            assertNotNull(cookie)
            assertEquals(1, cookie.size)
            assertEquals("jwt", cookie[0].name)
            assertNotEquals("", cookie[0].value)
        }
    }

    @Test
    fun testGetMeAfterLogout() = customTestApplicationWithLogin { client ->
        client.checkMeSuccess()
        client.logoutUser(false)
        client.checkMeSuccess()
        TestUser.accessToken = ""
        client.checkMeFailure()
    }

    @Test
    fun testGetMeAfterLogoutAll() = customTestApplicationWithLogin { client ->
        client.checkMeSuccess()

        val tokenVersion = transaction {
            val tokenVersion = UserEntity.all().first().tokenVersion
            assertEquals(1, tokenVersion)
            tokenVersion
        }

        client.logoutUser(true)

        transaction {
            val newTokenVersion = UserEntity.all().first().tokenVersion
            assertNotEquals(tokenVersion, newTokenVersion)
        }

        client.checkMeFailure()
    }

    @Test
    fun testRefreshTokenAfterLogout() = customTestApplication {
        var client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(HttpCookies) {
                storage = CustomCookieStorage()
            }
        }
        client.loginUser()
        client.checkMeSuccess()
        val cookies = client.cookies("http://localhost/refresh_token")
        assertEquals(1, cookies.size)
        client.logoutUser(false)

        client.get("refresh_token").let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<AuthToken> = response.body()
            val shouldResponse: APIResponse<AuthToken> = wrapFailure("Your refreshToken is absent.", 401)
            assertEquals(shouldResponse, responseBody)
        }

        assertEquals(0, client.cookies("http://localhost/refresh_token").size)

        client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(HttpCookies) {
                storage = ConstantCookiesStorage(cookies[0])
            }
        }
        client.get("refresh_token").let { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<AuthToken> = response.body()
            assert(responseBody.success)
            assertNull(responseBody.error)
            assertNotNull(responseBody.data)
        }
    }

    @Test
    fun testRefreshTokenAfterLogoutAll() = customTestApplication {
        var client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(HttpCookies) {
                storage = CustomCookieStorage()
            }
        }
        client.loginUser()
        client.checkMeSuccess()
        val cookies = client.cookies("http://localhost/refresh_token")
        assertEquals(1, cookies.size)
        client.logoutUser(true)
        assertEquals(0, client.cookies("http://localhost/refresh_token").size)

        client.get("refresh_token").let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<AuthToken> = response.body()
            val shouldResponse: APIResponse<AuthToken> = wrapFailure("Your refreshToken is absent.", 401)
            assertEquals(shouldResponse, responseBody)
        }

        client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(HttpCookies) {
                storage = ConstantCookiesStorage(cookies[0])
            }
        }

        client.get("refresh_token").let { response ->
            assertEquals(HttpStatusCode.Unauthorized, response.status)
            val responseBody: APIResponse<AuthToken> = response.body()
            val shouldResponse: APIResponse<AuthToken> = wrapFailure("Your refreshToken does not match.", 401)
            assertEquals(shouldResponse, responseBody)
        }
    }
}
