package com.boycott.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.boycott.app.R
import com.boycott.app.utils.LocaleUtils
import com.boycott.app.utils.LocaleInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectorDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLanguage = LocaleUtils.getStoredLanguage(context)
    val locales = LocaleUtils.getLocaleList()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column {
                TopAppBar(
                    title = { Text(stringResource(R.string.select_language)) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }
                )
                
                LazyColumn {
                    items(locales) { locale ->
                        LanguageItem(
                            locale = locale,
                            isSelected = locale.code == currentLanguage,
                            onClick = {
                                LocaleUtils.applyLanguage(context, locale.code)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageItem(
    locale: LocaleInfo,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = { Text(locale.displayName) },
        leadingContent = if (locale.isRTL) {
            { Text("RTL") }
        } else null,
        trailingContent = if (isSelected) {
            { Icon(Icons.Default.Check, contentDescription = null) }
        } else null,
        modifier = modifier.clickable(onClick = onClick)
    )
} 