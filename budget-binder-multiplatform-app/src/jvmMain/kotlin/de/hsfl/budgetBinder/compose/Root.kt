package de.hsfl.budgetBinder.compose

import androidx.compose.material.*
import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.StateManager.darkMode
import de.hsfl.budgetBinder.compose.StateManager.scaffoldState
import de.hsfl.budgetBinder.compose.navigation.AppBarComponent
import de.hsfl.budgetBinder.compose.navigation.DrawerContent
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
import de.hsfl.budgetBinder.presentation.viewmodel.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.compose.withDI
import org.kodein.di.instance

val di = DI {
    // scope
    bindSingleton { CoroutineScope(Dispatchers.Unconfined + SupervisorJob()) }

    // Client
    bindSingleton { Client(engine = CIO.create()) }

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

    // ViewModels
    bindSingleton { LoginViewModel(instance(), instance(), instance()) }
    bindSingleton { RegisterViewModel(instance(), instance(), instance(), instance()) }
    bindSingleton { SettingsViewModel(instance(), instance(), instance()) }
    bindSingleton {
        CategoryViewModel(
            instance(),
            instance(),
            instance(),
            instance(),
            instance(),
            instance(),
            instance()
        )
    }
    bindSingleton { EntryViewModel(instance(), instance(), instance(), instance(), instance(), instance()) }
    bindSingleton { DashboardViewModel(instance(), instance(), instance(), instance()) }
}

@Composable
fun App() = withDI(di) {
    val scaffold = remember { scaffoldState }
    val scope = rememberCoroutineScope()
    MaterialTheme(
        colors = if (darkMode.value) darkColors() else lightColors()
    ) {
        Scaffold(
            scaffoldState = scaffold,
            topBar = {
                if (!StateManager.isLoggedIn.value) AppBarComponent()
                else AppBarComponent(onMenuClicked = { toggleDrawerNav(scope) })
            },
            floatingActionButton = {},
            floatingActionButtonPosition = FabPosition.End,
            isFloatingActionButtonDocked = false,
        ) {
            ModalDrawer(drawerState = StateManager.drawerState,
                gesturesEnabled = StateManager.isLoggedIn.value,
                drawerContent = { DrawerContent() },
                content = { Router() })
        }
    }
}

private fun toggleDrawerNav(scope: CoroutineScope) {
    scope.launch {
        if (StateManager.drawerState.isOpen) StateManager.drawerState.close()
        else StateManager.drawerState.open()
    }
}