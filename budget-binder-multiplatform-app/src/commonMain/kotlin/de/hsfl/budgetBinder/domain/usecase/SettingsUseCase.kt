package de.hsfl.budgetBinder.domain.usecase

import de.hsfl.budgetBinder.data.repository.SettingsRepositoryImpl
import de.hsfl.budgetBinder.domain.repository.SettingsRepository

class StoreIsFirstTimeUseCase(private val repository: SettingsRepository) {
    operator fun invoke() = repository.storeFirstTimeUse()
}

class IsFirstTimeUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Boolean = repository.checkIsFirstTime()
}

class StoreDarkThemeUseCase(private val repository: SettingsRepository) {
    operator fun invoke(isDarkTheme: Boolean) = repository.storeDarkTheme(isDarkTheme)
}

class IsDarkThemeUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Boolean = repository.getDarkMode()
}

class StoreServerUrlUseCase(private val repository: SettingsRepository) {
    operator fun invoke(url: String) = repository.storeServerUrl(url)
}

class IsServerUrlStoredUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Boolean = repository.checkHasServerUrlKey()
}

class GetServerUrlUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): String = repository.getServerUrl()
}
