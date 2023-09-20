package me.singleneuron.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.singleneuron.common.binding.SettingsBinding
import me.singleneuron.common.helper.Platform
import me.singleneuron.common.helper.getPlatform
import me.singleneuron.common.resouce.defaultStringRes
import me.singleneuron.common.theme.isLight
import me.singleneuron.common.ui.component.Hyperlink
import me.singleneuron.licensesdialog.common.ComposeIssue
import me.singleneuron.licensesdialog.common.NoticeDialog
import me.singleneuron.licensesdialog.common.licenses.ApacheSoftwareLicense20
import me.singleneuron.licensesdialog.common.model.Notice

@OptIn(ComposeIssue::class)
@Composable
fun About(){
    var state = mutableStateOf(false)
    Card(
        Modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier.width(IntrinsicSize.Max)
        ) {
            AboutItem(@Composable {
                Text(defaultStringRes.about_author)
            } to @Composable {
                Text(defaultStringRes.about_cryolitia)
            })
            AboutItem(@Composable {
                Text(defaultStringRes.about_openSource)
            } to @Composable {
                Hyperlink("https://github.com/Cryolitia/PhotoTimeFix","Cryolitia/PhotoTimeFix")
            })
            AboutItem(@Composable {
                Text("Telegram: ")
            } to @Composable {
                Hyperlink("https://t.me/NeuronDevelopChannel","某元的原神頻道")
            })
            OutlinedButton(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                onClick = {
                    state.value = true
                }
            ) {
                Text(defaultStringRes.about_openSourceLicense)
            }

        }
    }
    NoticeDialog(defaultStringRes.about_openSourceLicense,
        mutableListOf(
            Notice("Compose Multiplatform","https://github.com/JetBrains/compose-jb","JetBrains", ApacheSoftwareLicense20),
            Notice("Kotlin","https://github.com/JetBrains/kotlin","Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.",ApacheSoftwareLicense20),
            Notice("Material Components for Android","https://github.com/material-components/material-components-android",null,ApacheSoftwareLicense20),
            Notice("Accompanist","https://github.com/google/accompanist","Copyright 2020 The Android Open Source Project",ApacheSoftwareLicense20),
            Notice("Android Open Source Project","Google Inc.","https://source.android.google.cn/setup/start/licenses?hl=zh-cn",ApacheSoftwareLicense20)
        ).apply {
                if (getPlatform()!=Platform.Android) {
                    add(Notice("metadata-extractor","https://github.com/drewnoakes/metadata-extractor","Copyright © 2002-2020 Drew Noakes. All Rights Reserved.",ApacheSoftwareLicense20))
                }
        },
        state,
        !isLight(),
        false,
        true    )
}

@Composable
fun AboutItem(
    content: Pair<@Composable ()->Unit,@Composable ()->Unit>
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content.first.invoke()
        Spacer(Modifier.width(8.dp))
        content.second.invoke()
    }
}