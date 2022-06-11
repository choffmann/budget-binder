import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import de.hsfl.budgetBinder.compose.Router
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.data.client.Client
import de.hsfl.budgetBinder.data.repository.AuthRepositoryImpl
import de.hsfl.budgetBinder.data.repository.UserRepositoryImpl
import de.hsfl.budgetBinder.domain.repository.AuthRepository
import de.hsfl.budgetBinder.domain.repository.UserRepository
import de.hsfl.budgetBinder.domain.use_case.auth_user.LoginUseCase
import de.hsfl.budgetBinder.domain.use_case.auth_user.LogoutUseCase
import de.hsfl.budgetBinder.domain.use_case.auth_user.RegisterUseCase
import de.hsfl.budgetBinder.domain.use_case.get_user.UserUseCase
import de.hsfl.budgetBinder.presentation.Screen
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.renderComposable
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.compose.withDI
import org.kodein.di.instance

val di = DI {
    bindSingleton { Client() }

    bindSingleton<AuthRepository> { AuthRepositoryImpl(instance()) }
    bindSingleton<UserRepository> { UserRepositoryImpl(instance()) }

    bindSingleton { RegisterUseCase(instance()) }
    bindSingleton { LoginUseCase(instance()) }
    bindSingleton { LogoutUseCase(instance()) }
    bindSingleton { UserUseCase(instance()) }
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
