package de.hsfl.budgetBinder.domain.repository

interface SettingsRepository {
    // First Time Use
    fun storeFirstTimeUse()
    fun checkIsFirstTime(): Boolean

    // Dark Mode
    fun storeDarkTheme(isDarkMode: Boolean)
    fun checkHasDarkModeKey(): Boolean
    fun getDarkMode(): Boolean

    // Server URL
    fun storeServerUrl(url: String)
    fun checkHasServerUrlKey(): Boolean
    fun getServerUrl(): String

    fun reset()
}
