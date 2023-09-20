package me.singleneuron.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.singleneuron.common.resouce.defaultStringRes
import me.singleneuron.common.binding.SettingsBinding as binding

@Composable
fun Settings() {
    Card(
        Modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier.width(IntrinsicSize.Max)
        ) {
            SettingItem(
                defaultStringRes.settings_safeMode,
                defaultStringRes.settings_safeMode_summary,
                binding.safeMode
            )
            SettingItem(
                defaultStringRes.settings_preferEXIF,
                defaultStringRes.settings_preferEXIF_summary,
                binding.preferEXIF
            )
            SettingItem(
                defaultStringRes.settings_mediaOnly,
                defaultStringRes.settings_mediaOnly_summary,
                binding.mediaOnly
            )
        }
    }
}

@Composable
fun SettingItem(
    name: String,
    summary: String = "",
    status: MutableState<Boolean>
) {
    var value by rememberSaveable { status }
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column (
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                name,
                style = MaterialTheme.typography.body1
            )
            Text(
                summary,
                style = MaterialTheme.typography.caption
            )
        }
        Spacer(Modifier.width(8.dp))
        Switch(
            checked = value,
            onCheckedChange = { value=it },
            colors = object : SwitchColors {
                /**
                 * Represents the color used for the switch's thumb, depending on [enabled] and [checked].
                 *
                 * @param enabled whether the [Switch] is enabled or not
                 * @param checked whether the [Switch] is checked or not
                 */
                @Composable
                override fun thumbColor(enabled: Boolean, checked: Boolean): State<Color> {
                    return rememberUpdatedState(if(checked) MaterialTheme.colors.primary else MaterialTheme.colors.surface )
                }

                /**
                 * Represents the color used for the switch's track, depending on [enabled] and [checked].
                 *
                 * @param enabled whether the [Switch] is enabled or not
                 * @param checked whether the [Switch] is checked or not
                 */
                @Composable
                override fun trackColor(enabled: Boolean, checked: Boolean): State<Color> {
                    return rememberUpdatedState(if(checked) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.onSurface.copy(alpha = 0.38f) )
                }

            }
        )
    }
}