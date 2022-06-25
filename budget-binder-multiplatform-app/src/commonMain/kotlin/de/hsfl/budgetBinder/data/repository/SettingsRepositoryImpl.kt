package de.hsfl.budgetBinder.data.repository

import com.russhwolf.settings.*
import de.hsfl.budgetBinder.domain.repository.SettingsRepository

object SettingsRepositoryImpl: SettingsRepository {
    private val settings: Settings = Settings()

    override fun storeFirstTimeUse() {
        settings.putBoolean("after_first_time_use", true)
    }

    override fun checkIsFirstTime(): Boolean {
        return !settings.hasKey("after_first_time_use")
    }

    override fun storeDarkTheme(isDarkMode: Boolean) {
        settings.putBoolean("is_dark_mode", isDarkMode)
    }

    override fun getDarkMode(): Boolean {
        return settings.getBoolean("is_dark_mode")
    }

    override fun storeServerUrl(url: String) {
        settings.putString("server_url", url)
    }

    override fun checkHasServerUrlKey(): Boolean {
        return settings.hasKey("server_url")
    }

    override fun getServerUrl(): String {
        return settings.getString("server_url", defaultValue = "http://localhost")
    }
}
