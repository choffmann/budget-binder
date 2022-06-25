package de.hsfl.budgetBinder.data.repository

import de.hsfl.budgetBinder.domain.repository.SettingsRepository

expect object SettingsRepositoryImpl : SettingsRepository {
    override fun storeFirstTimeUse()
    override fun checkIsFirstTime(): Boolean
    override fun storeDarkTheme(isDarkMode: Boolean)
    override fun getDarkMode(): Boolean
    override fun storeServerUrl(url: String)
    override fun checkHasServerUrlKey(): Boolean
    override fun getServerUrl(): String
}
