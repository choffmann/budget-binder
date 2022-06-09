package de.hsfl.budgetBinder.data.client

import de.hsfl.budgetBinder.common.*
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

// Define API Interfaces
interface ApiClient {

    // Auth
    suspend fun login(email: String, password: String): APIResponse<AuthToken>
    suspend fun register(user: User.In): APIResponse<User>
    suspend fun logout(onAllDevice: Boolean): APIResponse<AuthToken>

    // User
    suspend fun getMyUser(): APIResponse<User>
    suspend fun changeMyUser(user: User.In): APIResponse<User>
    suspend fun removeMyUser(): APIResponse<User>

    // Categories
    suspend fun getAllCategories(): APIResponse<List<Category>>
    suspend fun getAllCategories(period: String): APIResponse<Category>
    suspend fun createNewCategory(category: Category.In): APIResponse<Category>
    suspend fun getCategoryById(id: Int): APIResponse<Category>
    suspend fun changeCategoryById(category: Category.In, id: Int): APIResponse<Category>
    suspend fun removeCategoryById(id: Int): APIResponse<Category>
    suspend fun getEntriesFromCategory(id: Int): APIResponse<List<Category>>

    // Entries
    suspend fun getAllEntries(): APIResponse<List<Entry>>
    suspend fun getAllEntries(period: String): APIResponse<List<Entry>>
    suspend fun createNewEntry(entry: Entry.In): APIResponse<Entry>
    suspend fun getEntryById(id: Int): APIResponse<Entry>
    suspend fun changeEntryById(entry: Entry.In, id: Int): APIResponse<Entry>
    suspend fun removeEntryById(id: Int): APIResponse<Entry>
}

class Client(private val severUrl: String = Constants.BASE_URL) : ApiClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens("", "")
                }
                refreshTokens {
                    val refreshToken: APIResponse<AuthToken> =
                        client.get("/refresh_token") {
                            markAsRefreshTokenRequest()
                        }.body()
                    refreshToken.data?.let {
                        BearerTokens(it.token, "")
                    }
                }
            }
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
        }
        install(HttpCookies)

        defaultRequest {
            url(severUrl)
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

    override suspend fun register(user: User.In): APIResponse<User> {
        return client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.body()
    }

    override suspend fun logout(onAllDevice: Boolean): APIResponse<AuthToken> {
        return client.submitForm(
            url = "/logout", formParameters = Parameters.build {
                append("all", onAllDevice.toString())
            }, encodeInQuery = true
        ).body()
    }

    override suspend fun getMyUser(): APIResponse<User> {
        return client.get("/me").body()
    }

    override suspend fun changeMyUser(user: User.In): APIResponse<User> {
        return client.patch(urlString = "/me") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.body()
    }

    override suspend fun removeMyUser(): APIResponse<User> {
        return client.delete(urlString = "/me") {
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun getAllCategories(): APIResponse<List<Category>> {
        return client.submitForm(url = "/categories", formParameters = Parameters.build {
            append("current", "true")
        }, encodeInQuery = true).body()
    }

    override suspend fun getAllCategories(period: String): APIResponse<Category> {
        return client.submitForm(url = "/categories", formParameters = Parameters.build {
            append("period", period)
        }, encodeInQuery = true).body()
    }

    override suspend fun createNewCategory(category: Category.In): APIResponse<Category> {
        return client.post(urlString = "/categories") {
            contentType(ContentType.Application.Json)
            setBody(category)
        }.body()
    }

    override suspend fun getCategoryById(id: Int): APIResponse<Category> {
        return client.get(urlString = "/categories/$id").body()
    }

    override suspend fun changeCategoryById(category: Category.In, id: Int): APIResponse<Category> {
        return client.patch(urlString = "/categories/$id") {
            contentType(ContentType.Application.Json)
            setBody(category)
        }.body()
    }

    override suspend fun removeCategoryById(id: Int): APIResponse<Category> {
        return client.delete(urlString = "/categories/$id") {
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun getEntriesFromCategory(id: Int): APIResponse<List<Category>> {
        return client.get(urlString = "/categories/$id/entries").body()
    }

    override suspend fun getAllEntries(): APIResponse<List<Entry>> {
        return client.submitForm(url = "/entries", formParameters = Parameters.build {
            append("current", "true")
        }, encodeInQuery = true).body()
    }

    override suspend fun getAllEntries(period: String): APIResponse<List<Entry>> {
        return client.submitForm(url = "/categories", formParameters = Parameters.build {
            append("period", period)
        }, encodeInQuery = true).body()
    }

    override suspend fun createNewEntry(entry: Entry.In): APIResponse<Entry> {
        return client.post(urlString = "/entries") {
            contentType(ContentType.Application.Json)
            setBody(entry)
        }.body()
    }

    override suspend fun getEntryById(id: Int): APIResponse<Entry> {
        return client.get(urlString = "/entries/$id").body()
    }

    override suspend fun changeEntryById(entry: Entry.In, id: Int): APIResponse<Entry> {
        return client.patch(urlString = "/entries/$id") {
            contentType(ContentType.Application.Json)
            setBody(entry)
        }.body()
    }

    override suspend fun removeEntryById(id: Int): APIResponse<Entry> {
        return client.delete(urlString = "/entries/$id") {
            contentType(ContentType.Application.Json)
        }.body()
    }
}