package de.hsfl.budgetBinder.data.repository

import de.hsfl.budgetBinder.domain.repository.SettingsRepository

actual object SettingsRepositoryImpl : SettingsRepository {
    actual override fun storeFirstTimeUse() {}

    actual override fun checkIsFirstTime(): Boolean {
        return false
    }

    actual override fun storeDarkTheme(isDarkMode: Boolean) {}

    override fun checkHasDarkModeKey(): Boolean {
        return true
    }

    actual override fun getDarkMode(): Boolean {
        return false
    }

    actual override fun storeServerUrl(url: String) {}

    actual override fun checkHasServerUrlKey(): Boolean {
        return true
    }

    actual override fun getServerUrl(): String {
        return ""
    }
}
