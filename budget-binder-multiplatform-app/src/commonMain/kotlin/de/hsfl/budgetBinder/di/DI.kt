package de.hsfl.budgetBinder.di

import de.hsfl.budgetBinder.data.client.Client
import de.hsfl.budgetBinder.data.repository.AuthRepositoryImpl
import de.hsfl.budgetBinder.data.repository.CategoryRepositoryImpl
import de.hsfl.budgetBinder.data.repository.EntryRepositoryImpl
import de.hsfl.budgetBinder.data.repository.UserRepositoryImpl
import de.hsfl.budgetBinder.domain.repository.AuthRepository
import de.hsfl.budgetBinder.domain.repository.CategoryRepository
import de.hsfl.budgetBinder.domain.repository.EntryRepository
import de.hsfl.budgetBinder.domain.repository.UserRepository
import de.hsfl.budgetBinder.domain.usecase.*
import de.hsfl.budgetBinder.domain.usecase.storage.StoreDarkModeUseCase
import de.hsfl.budgetBinder.domain.usecase.storage.StoreServerUrlUseCase
import de.hsfl.budgetBinder.domain.usecase.storage.StoreUserStateUseCase
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import de.hsfl.budgetBinder.presentation.viewmodel.CategoryViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.login.LoginViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.register.RegisterViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.entry.EntryViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.navdrawer.NavDrawerViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.settings.SettingsEditServerUrlViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.settings.SettingsEditUserViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.settings.SettingsViewModel
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
    bindSingleton { Client(engine = ktorEngine) }

    // Repositories
    bindSingleton<AuthRepository> { AuthRepositoryImpl(instance()) }
    bindSingleton<UserRepository> { UserRepositoryImpl(instance()) }
    bindSingleton<CategoryRepository> { CategoryRepositoryImpl(instance()) }
    bindSingleton<EntryRepository> { EntryRepositoryImpl(instance()) }

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
    bindSingleton { LoginUseCases(instance(), instance()) }
    bindSingleton { DashboardUseCases(instance(), instance(), instance(), instance()) }
    bindSingleton { RegisterUseCases(instance(), instance(), instance()) }
    bindSingleton { DataFlowUseCases(instance(), instance(), instance()) }

    // Flows
    bindSingleton { RouterFlow(instance(), instance()) }
    bindSingleton { DataFlow(instance(), instance()) }
    bindSingleton { UiEventSharedFlow }

    // ViewModels
    bindSingleton { LoginViewModel(instance(), instance(), instance(), instance()) }
    bindSingleton { RegisterViewModel(instance(), instance(), instance(), instance()) }
    bindSingleton { SettingsViewModel(instance(), instance(), instance(), instance()) }
    bindSingleton { SettingsEditUserViewModel(instance(), instance(), instance(), instance()) }
    bindSingleton { SettingsEditServerUrlViewModel(instance(), instance(), instance()) }
    bindSingleton { CategoryViewModel(instance(), instance()) }
    bindSingleton { EntryViewModel(instance(), instance(), instance(), instance()) }
    bindSingleton { DashboardViewModel(instance(), instance(), instance(), instance()) }
    bindSingleton { NavDrawerViewModel(instance(), instance()) }
}
