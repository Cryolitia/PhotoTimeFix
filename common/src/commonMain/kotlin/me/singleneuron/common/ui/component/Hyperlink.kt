package me.singleneuron.common.ui.component

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle

@Composable
fun Hyperlink(
    url: String,
    content: String = url
) {
    val annotatedString = buildAnnotatedString {
        pushStringAnnotation(
            tag = "URL",
            annotation = url)
        withStyle(style = SpanStyle(
            color = MaterialTheme.colors.primary,
            fontFamily = FontFamily.Monospace
        )) {
            append(content)
        }
        pop()
    }

    val uriHandler = LocalUriHandler.current

    ClickableText(
        text = annotatedString,
        onClick = {offset: Int ->
            annotatedString.getStringAnnotations(tag = "URL",start = offset,end = offset).firstOrNull()?.let {
                uriHandler.openUri(it.item)
            }
        }
    )
}