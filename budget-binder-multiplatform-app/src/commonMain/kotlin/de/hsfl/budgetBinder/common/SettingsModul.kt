package de.hsfl.budgetBinder.common

import com.russhwolf.settings.Settings

class SettingsModul {
    val settings: Settings = Settings()

    // First Time Use
    fun storeFirstTimeUse() = settings.putBoolean("first_time_use", true)
    fun checkHasFirstTimeKey(): Boolean = settings.hasKey("first_time_use")

    // Server URL
    fun storeServerUrl(url: String) = settings.putString("server_url", url)
    fun checkHasServerUrl(): Boolean = settings.hasKey("server_url")
    fun getServerUrl(): String = settings.getString("server_url")
}
