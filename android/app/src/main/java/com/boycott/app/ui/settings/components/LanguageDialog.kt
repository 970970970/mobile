package com.boycott.app.ui.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.boycott.app.data.model.Language

@Composable
fun LanguageDialog(
    languages: List<Language>,
    currentLanguage: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (Language) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择语言") },
        text = {
            LazyColumn {
                items(languages) { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(language) }
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = language.flag,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = language.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = language.code,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (language.name == currentLanguage) {
                            Spacer(modifier = Modifier.weight(1f))
                            RadioButton(
                                selected = true,
                                onClick = null
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
} 