@file:JvmName("ThemeDesktopKt")

package me.singleneuron.common.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import kotlin.jvm.JvmName

@Composable
actual fun isLight(): Boolean = true

@Composable
actual fun getDynamicColors(isLight: Boolean): ColorScheme? {
    return null
}