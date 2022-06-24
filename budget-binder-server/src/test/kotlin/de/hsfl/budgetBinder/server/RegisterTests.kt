package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.utils.TestUser
import de.hsfl.budgetBinder.server.utils.customTestApplication
import de.hsfl.budgetBinder.server.utils.registerUser
import de.hsfl.budgetBinder.server.utils.wrapFailure
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*
import kotlin.test.assertEquals

class RegisterTests {
    @AfterTest
    fun deleteTestUser() = transaction {
        UserEntity.all().forEach { it.delete() }
    }

    @Test
    fun testRegisterWithoutBody() = customTestApplication { client ->
        client.post("/register").let { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<User> = response.body()
            val shouldResponse: APIResponse<User> = wrapFailure("The object you provided it not in the right format.")
            assertEquals(shouldResponse, responseBody)
        }
    }

    @Test
    fun testRegisterUser() = customTestApplication { client ->
        client.registerUser()
    }

    @Test
    fun testRegisterSameUser() = customTestApplication { client ->
        client.registerUser()
        client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(TestUser.userIn)
        }.let { response ->
            assertEquals(HttpStatusCode.OK, response.status)
            val responseBody: APIResponse<User> = response.body()
            val shouldResponse: APIResponse<User> = wrapFailure("Email already assigned. Please choose another.")
            assertEquals(shouldResponse, responseBody)
        }
    }
}
