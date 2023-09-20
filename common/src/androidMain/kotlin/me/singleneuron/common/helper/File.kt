package me.singleneuron.common.helper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import me.singleneuron.common.ui.component.FlowRow

@Composable
actual fun SelectFile(
    result: MutableState<String>,
    show: Boolean,
    onShown: ()->Unit
) {
}

actual class FileMMP actual constructor(path: String) {
    actual val fileState: FileState
        get() = TODO("Not yet implemented")
    actual val modifyDateTime: String
        get() = TODO("Not yet implemented")
}