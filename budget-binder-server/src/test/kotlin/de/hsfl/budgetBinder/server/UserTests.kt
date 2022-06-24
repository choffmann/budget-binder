package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.AuthToken
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.utils.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class UserTests {
    @BeforeTest
    fun registerTestUser() = customTestApplication { client ->
        client.registerUser()
    }

    @AfterTest
    fun deleteTestUser() = transaction {
        UserEntity.all().forEach { it.delete() }
    }

    @Test
    fun testGetMe() = customTestApplicationWithLogin { client ->
        client.checkMeSuccess()
    }

    @Test
    fun testPatchMeWithoutBody() = customTestApplicationWithLogin { client ->
        client.sendAuthenticatedRequest(HttpMethod.Patch, "/me") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val user: APIResponse<User> = response.body()
            val shouldUser: APIResponse<User> = wrapFailure("The object you provided it not in the right format.")
            assertEquals(shouldUser, user)
        }
    }

    @Test
    fun testPatchMe() = customTestApplicationWithLogin { client ->
        val userId = transaction { UserEntity.all().first().id.value }
        val patchedUser = User.Patch("changedTest", "changedSurname")

        client.sendAuthenticatedRequestWithBody(HttpMethod.Patch, "/me", patchedUser) { response ->
            assertEquals(HttpStatusCode.OK, response.status)

            val user: APIResponse<User> = response.body()
            val shouldUser = wrapSuccess(User(userId, "changedTest", "changedSurname", TestUser.email))
            assertEquals(shouldUser, user)

            transaction {
                val userEntity = UserEntity[userId]
                assertEquals("changedTest", userEntity.firstName)
                assertEquals("changedSurname", userEntity.name)
            }
        }
    }

    @Test
    fun testChangePassword() = customTestApplicationWithLogin { client ->
        val userId = transaction { UserEntity.all().first().id.value }
        val userPassword = transaction { UserEntity.all().first().passwordHash }

        val patchedUser = User.Patch(password = "newPassword")

        client.sendAuthenticatedRequestWithBody(HttpMethod.Patch, "/me", patchedUser) { response ->
            assertEquals(HttpStatusCode.OK, response.status)

            val user: APIResponse<User> = response.body()
            val shouldUser = wrapSuccess(TestUser.getTestUser(userId))
            assertEquals(shouldUser, user)

            transaction {
                val userEntity = UserEntity[userId]
                assertNotEquals(userPassword, userEntity.passwordHash)
            }
        }

        client.checkMeSuccess()

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
            val responseBody: APIResponse<AuthToken> = response.body()
            val shouldResponse: APIResponse<AuthToken> = wrapFailure("Your username and/or password do not match.", 401)
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testDeleteMe() = customTestApplicationWithLogin { client ->
        val userId = transaction { UserEntity.all().first().id.value }
        client.sendAuthenticatedRequest(HttpMethod.Delete, "/me") { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val user: APIResponse<User> = response.body()
            val shouldUser = wrapSuccess(TestUser.getTestUser(userId))
            assertEquals(shouldUser, user)

            transaction {
                assertNull(UserEntity.findById(userId))
            }
        }

        client.checkMeFailure()

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
            val responseBody: APIResponse<AuthToken> = response.body()
            val shouldResponse: APIResponse<AuthToken> = wrapFailure("Your username and/or password do not match.", 401)
            assertEquals(shouldResponse, responseBody)
        }
    }
}
