package me.singleneuron.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.singleneuron.common.binding.FileBinding
import me.singleneuron.common.helper.FileState
import me.singleneuron.common.resouce.defaultStringRes
import me.singleneuron.common.ui.component.FlowRow

@Composable
fun EditDateTime() {
    var file by remember { FileBinding.file }
    if (file.fileState == FileState.File) {
        Card (
            modifier = Modifier.width(IntrinsicSize.Max).height(IntrinsicSize.Max).padding(16.dp)
                ){
            Column(
                modifier = Modifier.width(IntrinsicSize.Max).height(IntrinsicSize.Max)
            ) {
                Row(
                    modifier = Modifier.height(IntrinsicSize.Max).fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(defaultStringRes.editDateTime_currentModify)
                    TextField(file.modifyDateTime,{}, enabled = false, readOnly = true)
                }
                OutlinedButton({

                },Modifier.fillMaxWidth().padding(8.dp)) {
                    Text(defaultStringRes.editDateTime_customFileProcessor)
                }
                Button({

                },Modifier.fillMaxWidth().padding(8.dp)) {
                    Text(defaultStringRes.editDateTime_start)
                }
            }
        }
    }
}