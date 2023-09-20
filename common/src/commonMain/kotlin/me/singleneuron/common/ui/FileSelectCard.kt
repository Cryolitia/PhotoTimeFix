package me.singleneuron.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collect
import me.singleneuron.common.helper.FileMMP
import me.singleneuron.common.helper.FileState
import me.singleneuron.common.helper.SelectFile
import me.singleneuron.common.resouce.defaultStringRes
import me.singleneuron.common.binding.FileBinding as binding

@Composable
fun FileSelectCard() {
    var path by rememberSaveable { binding.path }
    var openDialog by remember { mutableStateOf(false)  }
    var isError by remember { binding.isError }
    Card(
        Modifier.height(IntrinsicSize.Min).padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ){
            OutlinedTextField(
                value = path,
                onValueChange = { path = it },
                label = {
                    Text(defaultStringRes.fileSelectCard)
                },
                modifier = Modifier.defaultMinSize(minWidth = 300.dp).padding(8.dp),
                placeholder = { Text(defaultStringRes.fileSelectCard_placeHolder) },
                isError = isError
            )
            Button(
                onClick = { openDialog = true },
                modifier = Modifier.padding(8.dp)
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Image,"Image")
                    Text("|", fontSize = 18.sp, modifier = Modifier.padding(horizontal = 8.dp))
                    Icon(Icons.Outlined.PhotoLibrary,"Directory")
                }
            }
        }
    }
    SelectFile(binding.path, openDialog) {
        openDialog = false
    }
    LaunchedEffect(path){
        snapshotFlow { path }
            .collect {
                binding.file.value = FileMMP(it)
                binding.isError.value = (path.isNotEmpty()&&binding.file.value.fileState==FileState.NA)
            }
    }
}