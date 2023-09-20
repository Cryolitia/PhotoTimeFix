package me.singleneuron.common.binding

import androidx.compose.runtime.mutableStateOf
import me.singleneuron.common.helper.FileMMP
import me.singleneuron.common.helper.FileState

object FileBinding {
    val path = mutableStateOf("")
    var file = mutableStateOf(FileMMP(""))
    val isError = mutableStateOf(false)
}