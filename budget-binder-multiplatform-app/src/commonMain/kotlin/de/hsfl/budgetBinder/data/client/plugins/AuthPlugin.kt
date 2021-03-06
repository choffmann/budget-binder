package de.hsfl.budgetBinder.data.client.plugins

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.AuthToken
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*

class AuthPlugin private constructor(
    val loginPath: String,
    val logoutPath: String,
    val refreshPath: String,
    val pathsWithoutAuthorization: List<String>,
    val buildUrl: (path: String) -> String
) {
    var accessToken: String? = null

    class Config {
        var loginPath: String = "/login"
        var logoutPath: String = "/logout"
        var refreshPath: String = "/refresh_token"
        var buildUrlFunction: (path: String) -> String = { "http://localhost$it" }
        var pathsWithoutAuthorization: List<String> = listOf()

        fun addPathsWithoutAuthorization(vararg paths: String) {
            pathsWithoutAuthorization = paths.toList()
        }
    }

    companion object Plugin : HttpClientPlugin<Config, AuthPlugin> {
        override val key: AttributeKey<AuthPlugin> = AttributeKey("AuthPlugin")

        private val AuthPluginCircuitBreaker: AttributeKey<Unit> = AttributeKey("auth-plugin-request")

        override fun prepare(block: Config.() -> Unit): AuthPlugin {
            val config = Config().apply(block)

            return AuthPlugin(
                loginPath = config.loginPath,
                logoutPath = config.logoutPath,
                refreshPath = config.refreshPath,
                pathsWithoutAuthorization = config.pathsWithoutAuthorization,
                buildUrl = config.buildUrlFunction
            )
        }

        private fun appendAuthorizationHeader(plugin: AuthPlugin, request: HttpRequestBuilder) {
            request.headers {
                val tokenValue = "Bearer ${plugin.accessToken ?: ""}"
                if (contains(HttpHeaders.Authorization)) {
                    remove(HttpHeaders.Authorization)
                }
                append(HttpHeaders.Authorization, tokenValue)
            }
        }

        private fun checkLogout(plugin: AuthPlugin, call: HttpClientCall) {
            if (call.request.url.encodedPath == plugin.logoutPath) {
                if (call.response.status == HttpStatusCode.OK) {
                    plugin.accessToken = ""
                }
            }
        }

        @OptIn(InternalAPI::class)
        override fun install(plugin: AuthPlugin, scope: HttpClient) {
            scope.plugin(HttpSend).intercept { firstRequest ->
                if (firstRequest.attributes.contains(AuthPluginCircuitBreaker)) {
                    return@intercept execute(firstRequest)
                }
                val url = firstRequest.url.build()

                if (url.encodedPath == plugin.loginPath) {
                    val origin = execute(firstRequest)
                    if (origin.response.status == HttpStatusCode.OK) {
                        plugin.accessToken = scope.get(plugin.buildUrl(plugin.refreshPath)) {
                            attributes.put(AuthPluginCircuitBreaker, Unit)
                        }.body<APIResponse<AuthToken>>().data?.token
                    }
                    return@intercept origin
                }
                if (plugin.pathsWithoutAuthorization.any { url.encodedPath == it }) {
                    return@intercept execute(firstRequest)
                }

                // Authorization wird hinzugef??gt
                appendAuthorizationHeader(plugin, firstRequest)
                firstRequest.attributes.put(AuthPluginCircuitBreaker, Unit)

                var call = execute(firstRequest)

                if (call.response.status != HttpStatusCode.Unauthorized) {
                    checkLogout(plugin, call)
                    return@intercept call
                }

                // Unauthorized
                val refreshResponse = scope.get(plugin.buildUrl(plugin.refreshPath)) {
                    attributes.put(AuthPluginCircuitBreaker, Unit)
                }
                if (refreshResponse.status != HttpStatusCode.OK) {
                    return@intercept call
                }

                // Neuer Token
                plugin.accessToken = refreshResponse.body<APIResponse<AuthToken>>().data?.token

                val secondRequest = HttpRequestBuilder()
                secondRequest.takeFromWithExecutionContext(firstRequest)
                appendAuthorizationHeader(plugin, secondRequest)
                secondRequest.attributes.put(AuthPluginCircuitBreaker, Unit)

                call = execute(secondRequest)
                checkLogout(plugin, call)

                call
            }
        }
    }
}
