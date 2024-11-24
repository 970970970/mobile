package com.boycott.app.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.noties.markwon.Markwon
import android.widget.TextView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext

@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    val context = LocalContext.current
    val markwon = Markwon.builder(context).build()
    
    AndroidView(
        modifier = modifier,
        factory = { TextView(context) },
        update = { textView ->
            textView.setTextColor(color.hashCode())
            markwon.setMarkdown(textView, markdown)
        }
    )
} 