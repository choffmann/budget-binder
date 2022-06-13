import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.Router
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
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
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.login.LoginViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.*

import io.ktor.client.engine.js.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.renderComposable
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.compose.withDI
import org.kodein.di.instance


val di = DI {
    // scope
    bindSingleton { CoroutineScope(Dispatchers.Unconfined + SupervisorJob()) }

    // Client
    bindSingleton { Client(engine = Js.create()) }

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
    bindSingleton { RegisterViewModel(instance(), instance()) }
    bindSingleton { SettingsViewModel(instance(), instance()) }
    bindSingleton { CategoryViewModel(instance(), instance()) }
    bindSingleton { EntryViewModel(instance(), instance()) }
    bindSingleton { DashboardViewModel(instance(), instance()) }
}
fun main() {
    renderComposable("root") {
        Style(AppStylesheet)
        App() // Opens
    }
}
@Composable
fun App() = withDI(di) {
    val screenState = remember { mutableStateOf<Screen>(Screen.Login) }
    Router(screenState = screenState)
}
