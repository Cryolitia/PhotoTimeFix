@file:JvmName("ThemeAndroidKt")

package me.singleneuron.common.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun isLight(): Boolean {
    return !isSystemInDarkTheme()
}

@Composable
actual fun getDynamicColors(isLight: Boolean): ColorScheme? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
        if (isLight){
            dynamicLightColorScheme(LocalContext.current)
        } else {
            dynamicDarkColorScheme(LocalContext.current)
        }
    } else null
}