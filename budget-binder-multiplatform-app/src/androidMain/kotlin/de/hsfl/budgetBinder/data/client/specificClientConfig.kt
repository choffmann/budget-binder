package de.hsfl.budgetBinder.data.client

import de.hsfl.budgetBinder.data.client.plugins.FileCookieStorage
import io.ktor.client.*
import io.ktor.client.plugins.cookies.*

actual fun HttpClientConfig<*>.specificClientConfig() {
    install(HttpCookies) {
        storage = FileCookieStorage()
    }
}
