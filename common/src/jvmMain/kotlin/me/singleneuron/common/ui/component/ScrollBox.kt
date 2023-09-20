package me.singleneuron.common.ui.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun ScrollBox(modifier: Modifier, verticalScroll: ScrollState, horizontalScroll: Boolean, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
    ) {
        val stateVertical = rememberScrollState(0)
        val stateHorizontal = rememberScrollState(0)
        var boxModifier: Modifier = Modifier.width(IntrinsicSize.Max).height(IntrinsicSize.Max)
        if (false)
            boxModifier = boxModifier.verticalScroll(stateVertical)
        boxModifier = boxModifier.padding(end = 12.dp, bottom = 12.dp)
        if (horizontalScroll)
            boxModifier = boxModifier.horizontalScroll(stateHorizontal)
        Box(
            modifier = boxModifier
        ) {
            content()
        }
        if (false)
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(verticalScroll)
            )
        if (horizontalScroll)
            HorizontalScrollbar(
                modifier = Modifier.align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(end = 12.dp),
                adapter = rememberScrollbarAdapter(stateHorizontal)
            )
    }
}
