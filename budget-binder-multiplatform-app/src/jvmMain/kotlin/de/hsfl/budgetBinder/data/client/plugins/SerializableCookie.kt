package de.hsfl.budgetBinder.data.client.plugins

import io.ktor.http.*
import io.ktor.util.date.*
import java.io.Serializable

data class SerializableCookie(
    val name: String,
    val value: String,
    val encoding: CookieEncoding = CookieEncoding.URI_ENCODING,
    val maxAge: Int = 0,
    val expires: Long? = null,
    val domain: String? = null,
    val path: String? = null,
    val secure: Boolean = false,
    val httpOnly: Boolean = false,
    val extensions: Map<String, String?> = emptyMap()
) : Serializable {
    companion object {
        fun fromCookie(cookie: Cookie): SerializableCookie {
            return SerializableCookie(
                cookie.name,
                cookie.value,
                cookie.encoding,
                cookie.maxAge,
                cookie.expires?.timestamp,
                cookie.domain,
                cookie.path,
                cookie.secure,
                cookie.httpOnly,
                cookie.extensions
            )
        }
    }

    fun toCookie(): Cookie {
        return Cookie(
            name, value, encoding, maxAge, expires?.let { GMTDate(it) }, domain, path, secure, httpOnly, extensions
        )
    }
}
