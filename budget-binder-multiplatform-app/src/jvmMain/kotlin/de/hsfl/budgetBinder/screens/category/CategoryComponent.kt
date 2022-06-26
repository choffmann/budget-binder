package de.hsfl.budgetBinder.screens.category

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import org.kodein.di.instance

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CategoryComponent() {
    val routerFlow: RouterFlow by di.instance()
    val screenState = routerFlow.state.collectAsState()
    AnimatedContent(targetState = screenState.value, transitionSpec = {
        if (targetState.screenWeight > initialState.screenWeight) {
            slideInHorizontally { fullWidth -> fullWidth } + fadeIn() with slideOutHorizontally { fullWidth -> -fullWidth }
        } else {
            slideInHorizontally { fullWidth -> -fullWidth } + fadeIn() with slideOutHorizontally { fullWidth -> fullWidth }
        }.using(SizeTransform(clip = false))
    }) { state ->
        when (state) {
            is Screen.Category.Summary -> CategorySummary()
            is Screen.Category.Detail -> CategoryDetailView()
            is Screen.Category.Edit -> CategoryEditView()
            is Screen.Category.Create -> CategoryCreateView()
        }
    }
}
