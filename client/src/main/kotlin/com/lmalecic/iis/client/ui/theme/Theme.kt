package com.lmalecic.iis.client.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.jakewharton.mosaic.ui.Color

data class AppColorScheme(
    var primary: Color = Color.Unspecified,
)

private val LocalAppColorScheme = staticCompositionLocalOf {
    AppColorScheme()
}

private val DefaultColorScheme = AppColorScheme(
    primary = Color.Green
)

data class AppSymbols(
    var cursor: String = ""
)

private val LocalAppSymbols = staticCompositionLocalOf {
    AppSymbols()
}

private val DefaultAppSymbols = AppSymbols(
    cursor = "█"
)

object AppTheme {
    val colorScheme: AppColorScheme
        @Composable @ReadOnlyComposable
        get() = LocalAppColorScheme.current
    val symbols: AppSymbols
        @Composable @ReadOnlyComposable
        get() = LocalAppSymbols.current
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalAppColorScheme provides DefaultColorScheme,
        LocalAppSymbols provides DefaultAppSymbols,
        content = content,
    )
}