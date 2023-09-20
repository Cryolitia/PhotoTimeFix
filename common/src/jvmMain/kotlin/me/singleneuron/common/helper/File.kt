package me.singleneuron.common.helper

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.unit.dp
import java.io.File
import java.text.DateFormat
import java.util.Date
import javax.swing.JFileChooser
import javax.swing.JPanel
import javax.swing.UIManager

@Composable
actual fun SelectFile(
    result: MutableState<String>,
    show: Boolean,
    onShown: ()->Unit
) {
    val chooser = JFileChooser()
    SwingPanel(
        modifier = Modifier.size(0.dp,0.dp),
        factory = {
            //UIManager.setLookAndFeel(com.sun.java.swing.plaf.windows.WindowsLookAndFeel())
            val jpanel = JPanel()
            chooser.fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
            chooser.isMultiSelectionEnabled = false
            return@SwingPanel jpanel
        },
        update = {
            if (show) {
                val code = chooser.showOpenDialog(it)
                if (code == JFileChooser.APPROVE_OPTION) {
                    result.value = chooser.selectedFile.absolutePath
                }
                onShown()
            }
        }
    )

}

actual class FileMMP actual constructor(path: String) {

    private val file: File = File(path)

    actual val fileState: FileState
        get() {
            try {
                if (!file.exists())
                    return FileState.NA
                if (file.isDirectory)
                    return FileState.Directory
                return FileState.File
            } catch (e:Exception) {
                return FileState.NA
            }
        }

    actual val modifyDateTime: String
        get() = DateFormat.getDateTimeInstance().format(Date(file.lastModified()))

}