import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.Router
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.di.di
import de.hsfl.budgetBinder.presentation.Screen
import io.ktor.client.engine.js.*
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.renderComposable
import org.kodein.di.compose.withDI

fun main() {
    renderComposable("root") {
        Style(AppStylesheet)
        App() // Opens
    }
}

@Composable
fun App() = withDI(di(ktorEngine = Js.create())) {
    val screenState = remember { mutableStateOf<Screen>(Screen.Login) }
    Router(screenState = screenState)
}
