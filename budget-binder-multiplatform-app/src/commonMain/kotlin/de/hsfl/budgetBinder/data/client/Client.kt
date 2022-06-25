package de.hsfl.budgetBinder.data.client

import de.hsfl.budgetBinder.common.*
import de.hsfl.budgetBinder.data.client.plugins.AuthPlugin
import de.hsfl.budgetBinder.domain.usecase.GetServerUrlUseCase
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

// Define API Interfaces
interface ApiClient {

    /**
     * Login the User
     * @param email Email address from the user to login
     * @param password Password from the user to login
     * @author Cedrik Hoffmann
     */
    suspend fun login(email: String, password: String): APIResponse<AuthToken>

    /**
     * Register the User
     * @param user User to register
     * @author Cedrik Hoffmann
     */
    suspend fun register(user: User.In): APIResponse<User>

    /**
     * Logout the User
     * @param onAllDevice Parameter in URL, if the user should logout from all devices (true) or only on this device (false)
     * @author Cedrik Hoffmann
     */
    suspend fun logout(onAllDevice: Boolean): APIResponse<AuthToken>

    /**
     * Get information from the logged in user
     * @author Cedrik Hoffmann
     */
    suspend fun getMyUser(): APIResponse<User>

    /**
     * Change the current logged in user
     * @param user user object with the changes
     * @author Cedrik Hoffmann
     */
    suspend fun changeMyUser(user: User.Patch): APIResponse<User>

    /**
     * Delete the current logged in user
     * @author Cedrik Hoffmann
     */
    suspend fun deleteMyUser(): APIResponse<User>

    /**
     * Get all Categories from current month. The request has a query in the link ?current
     * @author Cedrik Hoffmann
     */
    suspend fun getAllCategories(): APIResponse<List<Category>>

    /**
     * Get all Categories from specific month.
     * @param period Period definition in format MM-YYYY (03-2022)
     * @author Cedrik Hoffmann
     */
    suspend fun getAllCategories(period: String): APIResponse<List<Category>>

    /**
     * Create a new Category
     * @param category Category to create
     * @author Cedrik Hoffmann
     */
    suspend fun createNewCategory(category: Category.In): APIResponse<Category>

    /**
     * Get a Category by ID
     * @param id ID of category to get
     * @author Cedrik Hoffmann
     */
    suspend fun getCategoryById(id: Int): APIResponse<Category>

    /**
     * Change Category by ID
     * @param category new category witch has the changes
     * @param id ID of category to change
     * @author Cedrik Hoffmann
     */
    suspend fun changeCategoryById(category: Category.Patch, id: Int): APIResponse<Category>

    /**
     * Delete Category by ID
     * @param id ID from Category to delete
     * @author Cedrik Hoffmann
     */
    suspend fun deleteCategoryById(id: Int): APIResponse<Category>

    /**
     * Get Entries from a Category ID. The request has a query in the link ?current
     * @param id ID from category to get the Entries. When ID null, the Api returns all entries with no category
     */
    suspend fun getEntriesFromCategory(id: Int?): APIResponse<List<Entry>>

    /**
     * Get Entries from a Category ID on period
     * @param id ID from category to get the Entries
     * @param period Period definition in format MM-YYYY (03-2022)
     */
    suspend fun getEntriesFromCategory(id: Int?, period: String): APIResponse<List<Entry>>

    /**
     * Get All Entries from current month. The request has a query in the link ?current
     * @author Cedrik Hoffmann
     */
    suspend fun getAllEntries(): APIResponse<List<Entry>>

    /**
     * Get all Entries from specific month.
     * @param period Period definition in format MM-YYYY (03-2022)
     * @author Cedrik Hoffmann
     */
    suspend fun getAllEntries(period: String): APIResponse<List<Entry>>

    /**
     * Create a new Entry
     * @param entry Entry to create
     * @author Cedrik Hoffmann
     */
    suspend fun createNewEntry(entry: Entry.In): APIResponse<Entry>

    /**
     * Get an entry by ID
     * @param id ID of entry to get
     * @author Cedrik Hoffmann
     */
    suspend fun getEntryById(id: Int): APIResponse<Entry>

    /**
     * Change Entry by ID
     * @param entry new entry witch has the changes
     * @param id ID of entry to change
     * @author Cedrik Hoffmann
     */
    suspend fun changeEntryById(entry: Entry.Patch, id: Int): APIResponse<Entry>

    /**
     * Delete Entry by ID
     * @param id ID from Entry to delete
     * @author Cedrik Hoffmann
     */
    suspend fun deleteEntryById(id: Int): APIResponse<Entry>
}

expect fun HttpClientConfig<*>.specificClientConfig()

/**
 * **Client**
 *
 * The Client to talk to the Backend. All available functions are list in ApiClient interface
 *
 * @param engine Engine for the Client
 * @author Cedrik Hoffmann
 * @see de.hsfl.budgetBinder.data.client.ApiClient
 */
class Client(
    private val engine: HttpClientEngine, private val getServerUrlUseCase: GetServerUrlUseCase
) : ApiClient {

    private val client = HttpClient(engine) {
        install(ContentNegotiation) {
            json()
        }

        install(AuthPlugin) {
            loginPath = "/login"
            logoutPath = "/logout"
            refreshPath = "/refresh_token"
            buildUrlFunction = { buildUrl(it) }
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
        }
        specificClientConfig()
    }

    override suspend fun login(email: String, password: String): APIResponse<AuthToken> {
        return client.submitForm(
            url = buildUrl("/login"), formParameters = Parameters.build {
                append("username", email)
                append("password", password)
            }, encodeInQuery = false
        ).body()
    }

    override suspend fun register(user: User.In): APIResponse<User> {
        return client.post(urlString = buildUrl("/register")) {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.body()
    }

    override suspend fun logout(onAllDevice: Boolean): APIResponse<AuthToken> {
        return client.submitForm(
            url = buildUrl("/logout"), formParameters = Parameters.build {
                append("all", onAllDevice.toString())
            }, encodeInQuery = true
        ).body()
    }

    override suspend fun getMyUser(): APIResponse<User> {
        return client.get(buildUrl("/me")).body()
    }

    override suspend fun changeMyUser(user: User.Patch): APIResponse<User> {
        return client.patch(urlString = buildUrl("/me")) {
            contentType(ContentType.Application.Json)
            setBody(user)
        }.body()
    }

    override suspend fun deleteMyUser(): APIResponse<User> {
        return client.delete(urlString = buildUrl("/me")) {
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun getAllCategories(): APIResponse<List<Category>> {
        return client.submitForm(url = buildUrl("/categories"), formParameters = Parameters.build {
            append("current", "true")
        }, encodeInQuery = true).body()
    }

    override suspend fun getAllCategories(period: String): APIResponse<List<Category>> {
        return client.submitForm(url = buildUrl("/categories"), formParameters = Parameters.build {
            append("period", period)
        }, encodeInQuery = true).body()
    }

    override suspend fun createNewCategory(category: Category.In): APIResponse<Category> {
        return client.post(urlString = buildUrl("/categories")) {
            contentType(ContentType.Application.Json)
            setBody(category)
        }.body()
    }

    override suspend fun getCategoryById(id: Int): APIResponse<Category> {
        return client.get(urlString = buildUrl("/categories/$id")).body()
    }

    override suspend fun changeCategoryById(category: Category.Patch, id: Int): APIResponse<Category> {
        return client.patch(urlString = buildUrl("/categories/$id")) {
            contentType(ContentType.Application.Json)
            setBody(category)
        }.body()
    }

    override suspend fun deleteCategoryById(id: Int): APIResponse<Category> {
        return client.delete(urlString = buildUrl("/categories/$id")) {
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun getEntriesFromCategory(id: Int?): APIResponse<List<Entry>> {
        return client.submitForm(url = buildUrl("/categories/$id/entries"), formParameters = Parameters.build {
            append("current", "true")
        }, encodeInQuery = true).body()
    }

    override suspend fun getEntriesFromCategory(id: Int?, period: String): APIResponse<List<Entry>> {
        return client.submitForm(url = buildUrl("/categories/$id/entries"), formParameters = Parameters.build {
            append("period", period)
        }, encodeInQuery = true).body()
    }

    override suspend fun getAllEntries(): APIResponse<List<Entry>> {
        return client.submitForm(url = buildUrl("/entries"), formParameters = Parameters.build {
            append("current", "true")
        }, encodeInQuery = true).body()
    }

    override suspend fun getAllEntries(period: String): APIResponse<List<Entry>> {
        return client.submitForm(url = buildUrl("/entries"), formParameters = Parameters.build {
            append("period", period)
        }, encodeInQuery = true).body()
    }

    override suspend fun createNewEntry(entry: Entry.In): APIResponse<Entry> {
        return client.post(urlString = buildUrl("/entries")) {
            contentType(ContentType.Application.Json)
            setBody(entry)
        }.body()
    }

    override suspend fun getEntryById(id: Int): APIResponse<Entry> {
        return client.get(urlString = buildUrl("/entries/$id")).body()
    }

    override suspend fun changeEntryById(entry: Entry.Patch, id: Int): APIResponse<Entry> {
        return client.patch(urlString = buildUrl("/entries/$id")) {
            contentType(ContentType.Application.Json)
            setBody(entry)
        }.body()
    }

    override suspend fun deleteEntryById(id: Int): APIResponse<Entry> {
        return client.delete(urlString = buildUrl("/entries/$id")) {
            contentType(ContentType.Application.Json)
        }.body()
    }

    private fun buildUrl(path: String): String {
        val serverUrl = getServerUrlUseCase()
        println("Server URL: $serverUrl")
        return serverUrl + path
    }
}
