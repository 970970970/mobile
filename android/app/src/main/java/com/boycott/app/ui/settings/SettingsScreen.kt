package com.boycott.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.boycott.app.R
import com.boycott.app.utils.LocaleUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onShowLanguageSelector: () -> Unit,
    onShowFeedback: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // 语言设置
            SettingsSection(title = stringResource(R.string.language_settings)) {
                SettingsItem(
                    title = stringResource(R.string.current_language),
                    subtitle = LocaleUtils.getLocaleList()
                        .find { it.code == uiState.currentLanguage }
                        ?.displayName ?: "",
                    icon = Icons.Default.Language,
                    onClick = onShowLanguageSelector
                )
            }
            
            // 主题设置
            SettingsSection(title = stringResource(R.string.theme_settings)) {
                SettingsItem(
                    title = stringResource(R.string.dark_mode),
                    icon = Icons.Default.DarkMode,
                    trailing = {
                        Switch(
                            checked = uiState.isDarkMode,
                            onCheckedChange = { viewModel.setDarkMode(it) }
                        )
                    }
                )
            }
            
            // 缓存管理
            SettingsSection(title = stringResource(R.string.cache_management)) {
                SettingsItem(
                    title = stringResource(R.string.clear_cache),
                    subtitle = uiState.cacheSize,
                    icon = Icons.Default.DeleteSweep,
                    onClick = { viewModel.clearCache(context) }
                )
            }
            
            // 反馈与支持
            SettingsSection(title = stringResource(R.string.feedback_support)) {
                SettingsItem(
                    title = stringResource(R.string.submit_feedback),
                    icon = Icons.Default.Feedback,
                    onClick = onShowFeedback
                )
                
                SettingsItem(
                    title = stringResource(R.string.community_discussion),
                    icon = Icons.Default.Forum,
                    onClick = { viewModel.openTelegramGroup() }
                )
                
                SettingsItem(
                    title = stringResource(R.string.support_us),
                    icon = Icons.Default.Favorite,
                    onClick = { viewModel.showDonationOptions() }
                )
            }
            
            // 关于
            SettingsSection(title = stringResource(R.string.about)) {
                SettingsItem(
                    title = stringResource(R.string.privacy_policy),
                    icon = Icons.Default.PrivacyTip,
                    onClick = { viewModel.showPrivacyPolicy() }
                )
                
                SettingsItem(
                    title = stringResource(R.string.terms_of_service),
                    icon = Icons.Default.Description,
                    onClick = { viewModel.showTermsOfService() }
                )
                
                SettingsItem(
                    title = stringResource(R.string.version),
                    subtitle = uiState.appVersion,
                    icon = Icons.Default.Info
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SettingsItem(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Surface(
        onClick = { onClick?.invoke() },
        enabled = onClick != null,
        modifier = modifier
    ) {
        ListItem(
            headlineContent = { Text(title) },
            supportingContent = subtitle?.let { { Text(it) } },
            leadingContent = { Icon(icon, contentDescription = null) },
            trailingContent = trailing
        )
    }
} 