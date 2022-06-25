package de.hsfl.budgetBinder.di

import de.hsfl.budgetBinder.data.client.Client
import de.hsfl.budgetBinder.data.repository.*
import de.hsfl.budgetBinder.domain.repository.*
import de.hsfl.budgetBinder.domain.usecase.*
import de.hsfl.budgetBinder.domain.usecase.storage.StoreDarkModeUseCase
import de.hsfl.budgetBinder.domain.usecase.storage.StoreServerUrlUseCase
import de.hsfl.budgetBinder.domain.usecase.storage.StoreUserStateUseCase
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import de.hsfl.budgetBinder.presentation.viewmodel.auth.login.LoginViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.auth.register.RegisterViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.category.summary.CategorySummaryViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.category._CategoryViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.category.create.CategoryCreateViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.category.detail.CategoryDetailViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.category.edit.CategoryEditViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.entry.EntryViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.navdrawer.NavDrawerViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.settings.SettingsEditServerUrlViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.settings.SettingsEditUserViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.settings.SettingsViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.welcome.WelcomeViewModel
import io.ktor.client.engine.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

fun kodein(ktorEngine: HttpClientEngine) = DI {
    // scope
    bindSingleton { CoroutineScope(Dispatchers.Unconfined + SupervisorJob()) }

    // Client
    bindSingleton { Client(engine = ktorEngine, instance()) }

    // Repositories
    bindSingleton<AuthRepository> { AuthRepositoryImpl(instance()) }
    bindSingleton<UserRepository> { UserRepositoryImpl(instance()) }
    bindSingleton<CategoryRepository> { CategoryRepositoryImpl(instance()) }
    bindSingleton<EntryRepository> { EntryRepositoryImpl(instance()) }
    bindSingleton<SettingsRepository> { SettingsRepositoryImpl }

    // SettingsUseCase
    bindSingleton { StoreIsFirstTimeUseCase(instance()) }
    bindSingleton { IsFirstTimeUseCase(instance()) }
    bindSingleton { StoreDarkThemeUseCase(instance()) }
    bindSingleton { IsDarkThemeUseCase(instance()) }
    bindSingleton { IsServerUrlStoredUseCase(instance()) }
    bindSingleton { GetServerUrlUseCase(instance()) }
    bindSingleton { StoreServerUrlUseCase(instance()) }

    // AuthUseCase
    bindSingleton { RegisterUseCase(instance()) }
    bindSingleton { LoginUseCase(instance()) }
    bindSingleton { LogoutUseCase(instance()) }

    // UserUseCase
    bindSingleton { GetMyUserUseCase(instance()) }
    bindSingleton { ChangeMyUserUseCase(instance()) }
    bindSingleton { DeleteMyUserUseCase(instance()) }

    // CategoriesUseCase
    bindSingleton { GetAllCategoriesUseCase(instance()) }
    bindSingleton { CreateCategoryUseCase(instance()) }
    bindSingleton { GetCategoryByIdUseCase(instance()) }
    bindSingleton { ChangeCategoryByIdUseCase(instance()) }
    bindSingleton { DeleteCategoryByIdUseCase(instance()) }
    bindSingleton { GetAllEntriesByCategoryUseCase(instance()) }

    // Entries
    bindSingleton { GetAllEntriesUseCase(instance()) }
    bindSingleton { CreateNewEntryUseCase(instance()) }
    bindSingleton { GetEntryByIdUseCase(instance()) }
    bindSingleton { ChangeEntryByIdUseCase(instance()) }
    bindSingleton { DeleteEntryByIdUseCase(instance()) }

    bindSingleton { NavigateToScreenUseCase() }
    bindSingleton { StoreUserStateUseCase() }
    bindSingleton { StoreServerUrlUseCase() }
    bindSingleton { StoreDarkModeUseCase() }
    bindSingleton { EntryUseCases(instance(), instance(), instance(), instance(), instance(), instance()) }
    bindSingleton { CategoriesUseCases(instance(), instance(), instance(), instance(), instance(), instance()) }
    bindSingleton { SettingsUseCases(instance(), instance(), instance()) }
    bindSingleton { AuthUseCases(instance(), instance(), instance(), instance(), instance(), instance()) }
    bindSingleton { DashboardUseCases(instance(), instance(), instance(), instance()) }
    bindSingleton { DataFlowUseCases(instance(), instance(), instance()) }

    // Flows
    bindSingleton { DataFlow(instance(), instance()) }
    bindSingleton { RouterFlow(instance(), instance(), instance()) }
    bindSingleton { UiEventSharedFlow }

    // ViewModels
    bindSingleton { WelcomeViewModel(instance(), instance()) }
    bindSingleton { LoginViewModel(instance(), instance(), instance(), instance()) }
    bindSingleton { RegisterViewModel(instance(), instance(), instance(), instance()) }
    bindSingleton { SettingsViewModel(instance(), instance(), instance(), instance()) }
    bindSingleton { SettingsEditUserViewModel(instance(), instance(), instance(), instance()) }
    bindSingleton { SettingsEditServerUrlViewModel(instance(), instance(), instance()) }
    bindSingleton { _CategoryViewModel(instance(), instance()) }
    bindSingleton { CategorySummaryViewModel(instance(), instance(), instance()) }
    bindSingleton { CategoryDetailViewModel(instance(), instance(), instance()) }
    bindSingleton { CategoryEditViewModel(instance(), instance(), instance()) }
    bindSingleton { CategoryCreateViewModel(instance(), instance(), instance()) }
    bindSingleton { EntryViewModel(instance(), instance(), instance()) }
    bindSingleton { DashboardViewModel(instance(), instance(), instance(), instance()) }
    bindSingleton { NavDrawerViewModel(instance(), instance(), instance()) }
}
