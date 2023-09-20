package me.singleneuron.common.binding

import androidx.compose.runtime.mutableStateOf

object SettingsBinding {
    val safeMode = mutableStateOf(true)
    val preferEXIF = mutableStateOf(true)
    val mediaOnly = mutableStateOf(true)
}