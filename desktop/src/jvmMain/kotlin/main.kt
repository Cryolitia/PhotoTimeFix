import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import me.singleneuron.common.resouce.defaultStringRes
import me.singleneuron.common.theme.AppTheme
import me.singleneuron.common.ui.*
import me.singleneuron.common.ui.component.FlowColumn

fun main() = application {
    val icon = painterResource("icon.jpg")
    Window(
        onCloseRequest = ::exitApplication,
        title = defaultStringRes.appName,
        state = rememberWindowState(width = Dp.Unspecified, height = Dp.Unspecified),
        icon = icon
    ) {
        Box(
            modifier = Modifier.padding(10.dp).width(IntrinsicSize.Min)
        ) {
            val stateHorizontal = rememberScrollState(0)
            Box(
                modifier = Modifier
                    .padding(end = 12.dp, bottom = 12.dp)
                    .horizontalScroll(stateHorizontal)
            ) {
                AppTheme {
                    FlowColumn {
                        FileSelectCard()
                        EditDateTime()
                        Settings()
                        About()
                        ExifDetail()
                    }
                }
            }
            HorizontalScrollbar(
                modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth()
                    .padding(end = 12.dp),
                adapter = rememberScrollbarAdapter(stateHorizontal)
            )
        }
    }
}