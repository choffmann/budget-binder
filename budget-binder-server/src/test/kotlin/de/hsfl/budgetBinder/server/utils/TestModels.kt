package de.hsfl.budgetBinder.server.utils

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.User
import io.ktor.client.plugins.cookies.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.util.date.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.min

object TestUser {
    const val email = "test@test.com"
    const val password = "test-test"
    const val firstName = "test"
    const val surName = "Test"

    val userIn = User.In(firstName, surName, email, password)

    fun getTestUser(id: Int): User {
        return User(id, firstName, surName, email)
    }

    var accessToken: String? = null
}

object TestCategories {

    const val color = "111111"
    val image = Category.Image.DEFAULT

}

class CustomCookieStorage : CookiesStorage {
    private val container: MutableList<Cookie> = mutableListOf()
    private var oldestCookie: Long = 0L
    private val mutex = Mutex()

    override suspend fun get(requestUrl: Url): List<Cookie> = mutex.withLock {
        val date = GMTDate()
        if (date.timestamp >= oldestCookie) cleanup(date.timestamp)

        return@withLock container.filter { isCookieForUrl(it, requestUrl) }
    }

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie): Unit = mutex.withLock {
        with(cookie) {
            if (name.isBlank()) return@withLock
        }

        val newCookie = cookie.fillDefaults(requestUrl)

        container.removeAll { areSameCookies(it, newCookie) }
        container.add(newCookie)

        cookie.expires?.timestamp?.let { expires ->
            if (oldestCookie > expires) {
                oldestCookie = expires
            }
        }
        cleanup(GMTDate().timestamp)
    }

    private fun areSameCookies(cookie: Cookie, other: Cookie): Boolean {
        val domain = cookie.domain?.toLowerCasePreservingASCIIRules()?.trimStart('.')
            ?: error("Domain field should have the default value")

        val otherDomain = other.domain?.toLowerCasePreservingASCIIRules()?.trimStart('.')
            ?: error("Domain field should have the default value")

        val path = cookie.path?.let {
            if (it.endsWith('/')) it else "$it/"
        } ?: error("Path field should have the default value")

        val otherPath = other.path?.let {
            if (it.endsWith('/')) it else "$it/"
        } ?: error("Path field should have the default value")

        return domain == otherDomain && path == otherPath && cookie.name == other.name
    }

    private fun isCookieForUrl(cookie: Cookie, requestUrl: Url): Boolean {
        val domain = cookie.domain?.toLowerCasePreservingASCIIRules()?.trimStart('.')
            ?: error("Domain field should have the default value")

        val path = cookie.path?.let {
            if (it.endsWith('/')) it else "$it/"
        } ?: error("Path field should have the default value")

        val host = requestUrl.host.toLowerCasePreservingASCIIRules()
        val requestPath = let {
            val pathInRequest = requestUrl.encodedPath
            if (pathInRequest.endsWith('/')) pathInRequest else "$pathInRequest/"
        }

        if (host != domain && (hostIsIp(host) || !host.endsWith(".$domain"))) {
            return false
        }

        if (path != "/" &&
            requestPath != path &&
            !requestPath.startsWith(path)
        ) return false

        return !(cookie.secure && !requestUrl.protocol.isSecure())
    }

    private fun Cookie.fillDefaults(requestUrl: Url): Cookie {
        var result = this

        if (result.path?.startsWith("/") != true) {
            result = result.copy(path = requestUrl.encodedPath)
        }

        if (result.domain.isNullOrBlank()) {
            result = result.copy(domain = requestUrl.host)
        }

        return result
    }

    private fun cleanup(timestamp: Long) {
        container.removeAll { cookie ->
            val expires = cookie.expires?.timestamp ?: return@removeAll false
            expires < timestamp
        }

        val newOldest = container.fold(Long.MAX_VALUE) { acc, cookie ->
            cookie.expires?.timestamp?.let { min(acc, it) } ?: acc
        }

        oldestCookie = newOldest
    }

    override fun close() {}
}
