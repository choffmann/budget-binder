package de.hsfl.budgetBinder.client

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.AuthToken
import de.hsfl.budgetBinder.common.User
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

class Client {
    private val bearerTokenStorage = mutableListOf<BearerTokens>()
    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
        install(Auth) {
            bearer {
                refreshTokens {
                    val refreshToken: APIResponse<AuthToken> =
                        client.get("http://localhost:8080/refresh_token") {
                            markAsRefreshTokenRequest()
                        }.body()
                    // cookie -> refresh_token
                    bearerTokenStorage.add(BearerTokens(refreshToken.data.token, ""))
                    bearerTokenStorage.last()
                }
            }
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
        }
        install(HttpCookies)
    }

    suspend fun authorize(username: String, password: String) {
        client.submitForm(
            url = "http://localhost:8080/login", formParameters = Parameters.build {
                append("username", username)
                append("password", password)
            }, encodeInQuery = false
        )
    }

    suspend fun getMyUserInfo(): APIResponse<User> {
        return client.get("http://localhost:8080/users/me").body()
    }
}