package de.hsfl.budgetBinder.data.client

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.AuthToken
import de.hsfl.budgetBinder.common.Constants
import de.hsfl.budgetBinder.common.User
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

class Client: ApiClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
        install(Auth) {
            bearer {
                refreshTokens {
                    val refreshToken: APIResponse<AuthToken> =
                        client.get("/refresh_token") {
                            markAsRefreshTokenRequest()
                        }.body()
                    BearerTokens(refreshToken.data.token, "")
                }
            }
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
        }
        install(HttpCookies)

        defaultRequest {
            url(Constants.BASE_URL)
        }
    }

    override suspend fun login(username: String, password: String): BearerTokens {
        val response: APIResponse<AuthToken> = client.submitForm(
            url = "/login", formParameters = Parameters.build {
                append("username", username)
                append("password", password)
            }, encodeInQuery = false
        ).body()
        return BearerTokens(response.data.token, "")
    }

    override suspend fun refreshToken(): BearerTokens {
        TODO("NOT IMPLEMENTED")
    }

    override suspend fun getMyUser(): User {
        return client.get("/users/me").body()
    }

    override suspend fun path(): String {
        return client.get("/path").body()
    }
}