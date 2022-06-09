package de.hsfl.budgetBinder.data.client

import de.hsfl.budgetBinder.common.*
import de.hsfl.budgetBinder.data.client.plugins.AuthPlugin
import de.hsfl.budgetBinder.data.client.plugins.FileCookieStorage
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

// Define API Interfaces
interface ApiClient {
    suspend fun login(email: String, password: String): APIResponse<AuthToken>

    suspend fun register(firstName: String, lastName: String, email: String, password: String): APIResponse<User>

    suspend fun logout(onAllDevice: Boolean)

    suspend fun getMyUser(): APIResponse<User>
}

class Client : ApiClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }

        install(AuthPlugin) {
            loginPath = "/login"
            logoutPath = "/logout"
            refreshPath = "/refresh_token"
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
        }
        install(HttpCookies) {
            storage = FileCookieStorage()
        }

        defaultRequest {
            url(Constants.BASE_URL)
        }
    }

    override suspend fun login(email: String, password: String): APIResponse<AuthToken> {
        return client.submitForm(
            url = "/login", formParameters = Parameters.build {
                append("username", email)
                append("password", password)
            }, encodeInQuery = false
        ).body()
    }

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): APIResponse<User> {
        return client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(User.In(firstName, lastName, email, password))
        }.body()
    }

    override suspend fun logout(onAllDevice: Boolean) {
        client.submitForm(
            url = "/logout", formParameters = Parameters.build {
                append("all", onAllDevice.toString())
            }, encodeInQuery = true
        )
    }

    override suspend fun getMyUser(): APIResponse<User> {
        return client.get("/me").body()
    }
}