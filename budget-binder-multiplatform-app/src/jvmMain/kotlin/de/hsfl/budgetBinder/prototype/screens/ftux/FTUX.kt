package de.hsfl.budgetBinder.prototype.screens.ftux

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.prototype.StateManager.selectedCategories

private val ftuxScreenState: MutableState<FTUXScreenState> = mutableStateOf(FTUXScreenState.ChooseCategories())

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FTUXComponent() {
    Column {
        AnimatedContent(targetState = ftuxScreenState.value, transitionSpec = {
            if (targetState.current > initialState.current) {
                slideInHorizontally { fullWidth -> fullWidth } + fadeIn() with slideOutHorizontally { fullWidth -> -fullWidth }
            } else {
                slideInHorizontally { fullWidth -> -fullWidth } + fadeIn() with slideOutHorizontally { fullWidth -> fullWidth }
            }.using(SizeTransform(clip = false))
        }) { state ->
            when (state) {
                is FTUXScreenState.ChooseCategories -> {
                    ChooseCategories(onContinue = {
                        ftuxScreenState.value = FTUXScreenState.SetBudget()
                    })
                }
                is FTUXScreenState.SetBudget -> {
                    SetBudget()
                }
            }
        }
    }
}

@Composable
internal fun TextHeader(modifier: Modifier = Modifier, title: String, text: String) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = title,
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(text = text, textAlign = TextAlign.Center)
    }
}

sealed class FTUXScreenState(val current: Int) {
    data class ChooseCategories(val position: Int = 0) : FTUXScreenState(position)
    data class SetBudget(val position: Int = 1) : FTUXScreenState(position)
}