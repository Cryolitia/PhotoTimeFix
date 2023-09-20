package me.singleneuron.common.helper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
expect fun SelectFile(
    result: MutableState<String>,
    show: Boolean,
    onShown: ()->Unit
)

expect class FileMMP(path: String) {
    val fileState: FileState
    val modifyDateTime: String
}

enum class FileState {
    File, Directory, NA
}