package de.hsfl.budgetBinder.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BudgetBar(modifier: Modifier = Modifier, progress: Float) {
    var _progress = progress
    if (progress > 1f) _progress = 1f
    if (progress < 0f) _progress = 0f
    val animatedProgress = animateFloatAsState(
        targetValue = _progress, animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value
    LinearProgressIndicator(modifier = modifier, progress = animatedProgress)
}
