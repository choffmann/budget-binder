import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.Router
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.di.kodein
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

val di = kodein(ktorEngine = Js.create())

@Composable
fun App() = withDI(di) {
    Router()
}
