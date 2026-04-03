package com.example.detetorsonolencia.ruben_morais.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4FC3F7),
    secondary = Color(0xFF29B6F6),
    background = Color(0xFF0A0E1A),
    surface = Color(0xFF1A2035),
    onPrimary = Color(0xFF0A0E1A),
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun DetetorSonolenciaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}