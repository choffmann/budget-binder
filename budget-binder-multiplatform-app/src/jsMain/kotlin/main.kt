import de.hsfl.budgetBinder.compose.ApplicationView
import org.jetbrains.compose.web.renderComposable

fun main() {
    renderComposable("root") {
        ApplicationView()
    }
}