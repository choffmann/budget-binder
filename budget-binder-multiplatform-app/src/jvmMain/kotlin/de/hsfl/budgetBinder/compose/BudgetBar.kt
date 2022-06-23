package de.hsfl.budgetBinder.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun BudgetBar(
    modifier: Modifier = Modifier, progress: Float, color: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = color.copy(alpha = ProgressIndicatorDefaults.IndicatorBackgroundOpacity)
) {
    var _progress = progress
    if (progress > 1f) _progress = 1f
    if (progress < 0f) _progress = 0f
    val animatedProgress = animateFloatAsState(
        targetValue = _progress, animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value
    LinearProgressIndicator(
        modifier = modifier,
        progress = animatedProgress,
        color = color,
        backgroundColor = backgroundColor
    )
}
