package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.AuthToken
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.utils.customTestApplication
import de.hsfl.budgetBinder.server.utils.loginUser
import de.hsfl.budgetBinder.server.utils.registerUser
import de.hsfl.budgetBinder.server.utils.wrapFailure
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.HttpCookie
import kotlin.test.*

class LoginTests {

    @BeforeTest
    fun registerTestUser() = customTestApplication { client ->
        client.registerUser()
    }

    @AfterTest
    fun deleteTestUser() = transaction {
        UserEntity.all().forEach { it.delete() }
    }

    @Test
    fun testLoginUnauthorized() = customTestApplication { client ->
        client.post("/login").let {
            assertEquals(HttpStatusCode.Unauthorized, it.status)
            val responseBody: APIResponse<AuthToken> = it.body()
            val shouldResponse: APIResponse<AuthToken> = wrapFailure("Your username and/or password do not match.", 401)
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testLoginFalseData() = customTestApplication { client ->
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
            val shouldResponse: APIResponse<AuthToken> = wrapFailure("Your username and/or password do not match.", 401)
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testLoginSuccessFull() = customTestApplication { client ->
        client.loginUser { response ->
            val setCookieHeader = response.headers[HttpHeaders.SetCookie]
            assertNotNull(setCookieHeader)
            val cookie = HttpCookie.parse(setCookieHeader)
            assertNotNull(cookie)
            assertEquals(1, cookie.size)
            assertEquals("jwt", cookie[0].name)
            assertNotEquals("", cookie[0].value)
        }
    }
}
