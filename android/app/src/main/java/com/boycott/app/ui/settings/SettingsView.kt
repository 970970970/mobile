package com.boycott.app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.boycott.app.R
import com.boycott.app.ui.settings.components.SettingsItem
import com.boycott.app.ui.settings.components.SettingsSection
import com.boycott.app.ui.settings.components.LanguageDialog
import com.boycott.app.ui.settings.components.FeedbackDialog

@Composable
fun SettingsView(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToUserAgreement: () -> Unit
) {
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    val languages by viewModel.languages.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val cacheSize by viewModel.cacheSize.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 基本设置
        item {
            SettingsSection(title = stringResource(R.string.settings_basic)) {
                // 语言设置
                SettingsItem(
                    icon = Icons.Default.Language,
                    title = stringResource(R.string.settings_language),
                    subtitle = currentLanguage,
                    onClick = { showLanguageDialog = true }
                )

                // 主题设置
                SettingsItem(
                    icon = Icons.Default.DarkMode,
                    title = stringResource(R.string.settings_dark_mode),
                    trailing = {
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { viewModel.setDarkMode(it) }
                        )
                    }
                )

                // 缓存管理
                SettingsItem(
                    icon = Icons.Default.Storage,
                    title = stringResource(R.string.settings_cache),
                    subtitle = "${cacheSize}KB",
                    onClick = { viewModel.clearCache(context) }
                )
            }
        }

        // 反馈与支持
        item {
            SettingsSection(title = stringResource(R.string.settings_feedback_support)) {
                SettingsItem(
                    icon = Icons.Default.Feedback,
                    title = stringResource(R.string.settings_submit_feedback),
                    onClick = { showFeedbackDialog = true }
                )
                SettingsItem(
                    icon = Icons.Default.Forum,
                    title = stringResource(R.string.settings_community),
                    onClick = { viewModel.openCommunity() }
                )
                SettingsItem(
                    icon = Icons.Default.Favorite,
                    title = stringResource(R.string.settings_donate),
                    onClick = { viewModel.openDonateArticle() }
                )
            }
        }

        // 关于
        item {
            SettingsSection(title = stringResource(R.string.settings_about)) {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = stringResource(R.string.settings_version),
                    subtitle = viewModel.getAppVersion(context)
                )
            }
        }

        // 法律条款
        item {
            SettingsSection(title = stringResource(R.string.settings_legal)) {
                SettingsItem(
                    icon = Icons.Default.Security,
                    title = stringResource(R.string.settings_privacy),
                    onClick = { onNavigateToPrivacyPolicy() }
                )
                SettingsItem(
                    icon = Icons.Default.Description,
                    title = stringResource(R.string.settings_agreement),
                    onClick = { onNavigateToUserAgreement() }
                )
            }
        }

        // 底部留白
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // 语言选择对话框
    if (showLanguageDialog) {
        LanguageDialog(
            languages = languages,
            currentLanguage = currentLanguage,
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { language ->
                viewModel.setLanguage(language.code)
                showLanguageDialog = false
            }
        )
    }

    // 反馈对话框
    if (showFeedbackDialog) {
        FeedbackDialog(
            onDismiss = { showFeedbackDialog = false },
            onSubmit = { type, content, contact, images ->
                viewModel.submitFeedback(type, content, contact, images)
                showFeedbackDialog = false
            }
        )
    }
} 