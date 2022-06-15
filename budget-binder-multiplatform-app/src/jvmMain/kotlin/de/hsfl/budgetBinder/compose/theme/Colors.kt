package de.hsfl.budgetBinder.compose.theme

import androidx.compose.material.darkColors
import androidx.compose.ui.graphics.Color

object LightColors {
    val primary = Color(0x6200EE)
    val primaryVariant = Color(0x3700B3)
    val secondary = Color(0x03DAC6)
    val secondaryVariant = Color(0x018786)
    val background = Color(0xFFFFFF)
    val surface = Color(0xFFFFFF)
    val error = Color(0xB00020)
    val onPrimary = Color(0xFFFFFF)
    val onSecondary = Color(0x000000)
    val onBackground = Color(0x000000)
    val onSurface = Color(0x000000)
    val onError = Color(0xFFFFFF)
}

object DarkColors {
    val primary = Color(0xBB86FC)
    val primaryVariant = Color(0x3700B3)
    val secondary = Color(0x03DAC5)
    val secondaryVariant = Color(0x00A895)
    val background = Color(0x121212)
    val surface = Color(0x121212)
    val error = Color(0xCF6679)
    val onPrimary = Color(0x000000)
    val onSecondary = Color(0x000000)
    val onBackground = Color(0xFFFFFF)
    val onSurface = Color(0xFFFFFF)
    val onError = Color(0x000000)
}

val DarkColorPlatte = darkColors(
    primary = DarkColors.primary,
    primaryVariant = DarkColors.primaryVariant,
    secondary = DarkColors.secondary,
    secondaryVariant = DarkColors.secondaryVariant,
    background = DarkColors.background,
    surface = DarkColors.surface,
    error = DarkColors.error,
    onPrimary = DarkColors.onPrimary,
    onSecondary = DarkColors.onSecondary,
    onBackground = DarkColors.onBackground,
    onSurface = DarkColors.onSurface,
    onError = DarkColors.onError
)

val LightColorPlatte = darkColors(
    primary = LightColors.primary,
    primaryVariant = LightColors.primaryVariant,
    secondary = LightColors.secondary,
    secondaryVariant = LightColors.secondaryVariant,
    background = LightColors.background,
    surface = LightColors.surface,
    error = LightColors.error,
    onPrimary = LightColors.onPrimary,
    onSecondary = LightColors.onSecondary,
    onBackground = LightColors.onBackground,
    onSurface = LightColors.onSurface,
    onError = LightColors.onError
)
