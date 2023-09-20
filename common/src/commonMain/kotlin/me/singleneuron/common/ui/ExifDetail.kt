package me.singleneuron.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import me.singleneuron.common.binding.FileBinding
import me.singleneuron.common.helper.FileState
import me.singleneuron.common.helper.getExif
import me.singleneuron.common.ui.component.*
import me.singleneuron.licensesdialog.common.DialogMMP
import kotlin.random.Random

@Composable
fun ExifDetail() {

    var path by remember { FileBinding.path }
    var file by remember { FileBinding.file }
    var exifMap by remember { mutableStateOf(mapOf<String,List<String>>()) }

    if (file.fileState==FileState.File) {
        Card(
            Modifier.defaultMinSize(minWidth = 50.dp).fillMaxWidth().height(IntrinsicSize.Min).padding(16.dp)
        ) {
            var showProcessingDialog by remember { mutableStateOf(true) }
            var switchMap = remember { mutableStateMapOf<String, Boolean>() }
            LaunchedEffect(path) {
                showProcessingDialog = true
                val map = mutableMapOf<String, ArrayList<String>>()
                getExif(path).collect {
                    if (map[it.first] == null) {
                        map[it.first] = arrayListOf()
                    }
                    map[it.first]!!.add(it.second)
                }
                exifMap = map
                showProcessingDialog = false
            }
            if (showProcessingDialog) {
                DialogMMP({}, "Processing……") {
                    CircularProgressIndicator(modifier = Modifier.width(Dp.Unspecified).height(Dp.Unspecified))
                }
            }
            Column(modifier = Modifier.width(500.dp)) {
                FlowRow {
                    for (entry in exifMap) {
                        if (switchMap[entry.key]==null) {
                            switchMap[entry.key] = true
                        }
                        MaterialTheme {
                            FilterChip(selected = switchMap[entry.key] ?: true, onClick = {
                                switchMap[entry.key] = !(switchMap[entry.key] ?: true)
                            }, modifier = Modifier.padding(start = 8.dp, end = 8.dp), selectedIcon = {
                                                                                                     Icon(Icons.Filled.Done,null, Modifier.padding(2.dp),MaterialTheme.colors.onPrimary)
                            }, colors = object : SelectableChipColors {
                                /**
                                 * Represents the background color for this chip, depending on [enabled] and [selected].
                                 *
                                 * @param enabled whether the chip is enabled
                                 * @param selected whether the chip is selected
                                 */
                                @Composable
                                override fun backgroundColor(enabled: Boolean, selected: Boolean): State<Color> {
                                    return rememberUpdatedState(if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.surface)
                                }

                                /**
                                 * Represents the content color for this chip, depending on [enabled] and [selected].
                                 *
                                 * @param enabled whether the chip is enabled
                                 * @param selected whether the chip is selected
                                 */
                                @Composable
                                override fun contentColor(enabled: Boolean, selected: Boolean): State<Color> {
                                    return rememberUpdatedState(if (selected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface)
                                }

                                /**
                                 * Represents the leading icon color for this chip, depending on [enabled] and [selected].
                                 *
                                 * @param enabled whether the chip is enabled
                                 * @param selected whether the chip is selected
                                 */
                                @Composable
                                override fun leadingIconColor(enabled: Boolean, selected: Boolean): State<Color> {
                                    return rememberUpdatedState(if (selected) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface)
                                }

                            }) {
                                Text(entry.key)
                            }
                        }
                    }
                }
                val scrollState = rememberScrollState(0)
                ScrollBox(
                    Modifier.defaultMinSize(minHeight = 500.dp),
                    verticalScroll = scrollState,
                    horizontalScroll = true
                ) {
                    SelectionContainer {
                        Column(Modifier.width(IntrinsicSize.Max).height(IntrinsicSize.Max).padding(8.dp)
                            .verticalScroll(scrollState)) {
                            for (entry in exifMap) {
                                if (switchMap[entry.key] != false) {
                                    for (string in entry.value) {
                                        Text("[${entry.key}] $string", modifier = Modifier.width(IntrinsicSize.Max))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}