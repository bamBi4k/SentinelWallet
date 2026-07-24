package com.sentinel.wallet.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7C8CFF),
    secondary = Color(0xFF2ECC71),
    tertiary = Color(0xFFFF5D6C),
    background = Color(0xFF07111F),
    surface = Color(0xFF0F192A),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFEDF3FB),
    onSurface = Color(0xFFEDF3FB),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4A6CF7),
    secondary = Color(0xFF2ECC71),
    tertiary = Color(0xFFFF5D6C),
    background = Color(0xFFF5F7FA),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1A1A2E),
    onSurface = Color(0xFF1A1A2E),
)

@Composable
fun SentinelWalletTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(
            headlineMedium = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        ),
        content = content
    )
}