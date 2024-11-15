package com.boycott.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.boycott.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackForm(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedbackViewModel = hiltViewModel()
) {
    var content by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.submit_feedback)) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.submitFeedback(content, contact)
                        },
                        enabled = content.isNotBlank() && !uiState.isSubmitting
                    ) {
                        Text(stringResource(R.string.submit))
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text(stringResource(R.string.feedback_content)) },
                placeholder = { Text(stringResource(R.string.feedback_hint)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                enabled = !uiState.isSubmitting
            )
            
            OutlinedTextField(
                value = contact,
                onValueChange = { contact = it },
                label = { Text(stringResource(R.string.contact_info)) },
                placeholder = { Text(stringResource(R.string.contact_hint)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSubmitting
            )
            
            if (uiState.isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
    
    // 显示提交结果
    LaunchedEffect(uiState.submitResult) {
        uiState.submitResult?.let { result ->
            when (result) {
                is FeedbackResult.Success -> {
                    // TODO: 显示成功提示
                    onDismiss()
                }
                is FeedbackResult.Error -> {
                    // TODO: 显示错误提示
                }
            }
        }
    }
} 