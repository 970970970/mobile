package com.boycott.app.ui.settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.boycott.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackDialog(
    onDismiss: () -> Unit,
    onSubmit: (type: String, content: String, contact: String?, images: List<String>?) -> Unit
) {
    var selectedType by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var images by remember { mutableStateOf<List<String>>(emptyList()) }

    val feedbackTypeIssue = stringResource(R.string.feedback_type_issue)

    LaunchedEffect(Unit) {
        selectedType = feedbackTypeIssue
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.feedback_title)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = { }
                ) {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.feedback_type)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text(stringResource(R.string.feedback_content)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text(stringResource(R.string.feedback_contact)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSubmit(selectedType, content, contact.takeIf { it.isNotBlank() }, images.takeIf { it.isNotEmpty() })
                }
            ) {
                Text(stringResource(R.string.feedback_submit))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.feedback_cancel))
            }
        }
    )
} 