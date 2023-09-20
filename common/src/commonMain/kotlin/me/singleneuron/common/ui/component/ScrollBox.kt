package me.singleneuron.common.ui.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun ScrollBox(modifier: Modifier = Modifier, verticalScroll: ScrollState, horizontalScroll: Boolean, content: @Composable BoxScope.() -> Unit)