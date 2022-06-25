package de.hsfl.budgetBinder.data.repository

import com.russhwolf.settings.*
import de.hsfl.budgetBinder.domain.repository.SettingsRepository

actual object SettingsRepositoryImpl: SettingsRepository {
    @OptIn(ExperimentalSettingsImplementation::class)
    private val settings: Settings = Settings()

    actual override fun storeFirstTimeUse() {
        settings.putBoolean("after_first_time_use", true)
    }

    actual override fun checkIsFirstTime(): Boolean {
        return !settings.hasKey("after_first_time_use")
    }

    actual override fun storeDarkTheme(isDarkMode: Boolean) {
        settings.putBoolean("is_dark_mode", isDarkMode)
    }

    actual override fun getDarkMode(): Boolean {
        return settings.getBoolean("is_dark_mode")
    }

    actual override fun storeServerUrl(url: String) {
        settings.putString("server_url", url)
    }

    actual override fun checkHasServerUrlKey(): Boolean {
        return settings.hasKey("server_url")
    }

    actual override fun getServerUrl(): String {
        return settings.getString("server_url", defaultValue = "http://localhost")
    }
}
