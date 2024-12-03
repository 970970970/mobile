package com.boycott.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.boycott.app.R
import dev.jeziellago.compose.markdowntext.MarkdownText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyView(
    viewModel: PrivacyPolicyViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    var content by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.content.collect { result ->
            when (result) {
                is Result.Success -> {
                    content = result.data
                    isLoading = false
                    error = null
                }
                is Result.Error -> {
                    error = result.message
                    isLoading = false
                }
                is Result.Loading -> {
                    isLoading = true
                    error = null
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_privacy_policy)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                    Text(
                        text = stringResource(R.string.settings_loading),
                        modifier = Modifier.padding(16.dp)
                    )
                }
                error != null -> Text(
                    text = stringResource(R.string.settings_error),
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.error
                )
                else -> MarkdownText(
                    markdown = content,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
} 