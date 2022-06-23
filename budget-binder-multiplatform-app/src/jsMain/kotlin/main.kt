import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.FeedbackSnackbar
import de.hsfl.budgetBinder.compose.Router
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.di.kodein
import de.hsfl.budgetBinder.presentation.event.UiEvent
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import io.ktor.client.engine.js.*
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.renderComposable
import org.kodein.di.compose.withDI
import org.kodein.di.instance

fun main() {
    renderComposable("root") {
        Style(AppStylesheet)
        App() // Opens
    }
}

val di = kodein(ktorEngine = Js.create())

@Composable
fun App() = withDI(di) {
    val uiEventFlow: UiEventSharedFlow by di.instance()
    val loadingState = remember { mutableStateOf(false) }
    val snackBarText = remember { mutableStateOf("") }
    val snackBarHidden = remember { mutableStateOf(true) }
    LaunchedEffect(key1 = true) {
        uiEventFlow.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowLoading -> loadingState.value = true
                is UiEvent.ShowError -> {
                    loadingState.value = false
                    snackBarText.value = event.msg
                    snackBarHidden.value = false
                }
                is UiEvent.ShowSuccess -> {
                    loadingState.value = false
                    snackBarText.value = event.msg
                    snackBarHidden.value = false
                }
                else -> {}
            }
        }
    }
    if (loadingState.value && snackBarHidden.value) {
        snackBarText.value = "Loading"
        snackBarHidden.value = false
        console.log("loading now! ${snackBarHidden.value}")
    }
    FeedbackSnackbar(snackBarText.value,snackBarHidden.value) {
        snackBarHidden.value = true
        loadingState.value = false
    }
    Router()
}
