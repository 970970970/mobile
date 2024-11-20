package com.boycott.app.ui.articles

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.boycott.app.data.model.Article
import com.boycott.app.ui.settings.components.FeedbackDialog
import com.boycott.app.utils.Result
import dev.jeziellago.compose.markdowntext.MarkdownText
import com.boycott.app.utils.TranslationMessages

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailView(
    viewModel: ArticleDetailViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    var showFeedbackDialog by remember { mutableStateOf(false) }
    val article by viewModel.article.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showFeedbackDialog = true }) {
                        Icon(Icons.Default.Feedback, contentDescription = "反馈")
                    }
                    IconButton(
                        onClick = {
                            when (article) {
                                is Result.Success -> {
                                    val shareIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TITLE, (article as Result.Success<Article>).data.title)
                                        putExtra(Intent.EXTRA_TEXT, (article as Result.Success<Article>).data.summary)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "分享文章"))
                                }
                                else -> {}
                            }
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "分享")
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
            when (article) {
                is Result.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is Result.Error -> {
                    Text(
                        text = (article as Result.Error).message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is Result.Success -> {
                    val articleData = (article as Result.Success<Article>).data
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // 标题
                        item {
                            Text(
                                text = articleData.title,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                        
                        // 发布时间
                        item {
                            articleData.publishedAt?.let { publishedAt ->
                                Text(
                                    text = "发布时间：$publishedAt",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }
                        }

                        // 摘要
                        item {
                            articleData.summary?.let { summary ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "摘要",
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                        Text(
                                            text = summary,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }

                        // 翻译提示（非中文内容）
                        item {
                            if (articleData.language != "Chinese") {
                                val warningMessage = TranslationMessages.aiTranslationWarning[articleData.language] 
                                    ?: TranslationMessages.aiTranslationWarning["English"]!! // 默认使用英文
                                
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Translate,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(
                                            text = warningMessage,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }
                        }

                        // 图片
                        item {
                            articleData.image?.let { imageUrl ->
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = articleData.title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                        .padding(bottom = 16.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        // 内容
                        item {
                            articleData.content?.let { content ->
                                MarkdownText(
                                    markdown = content,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

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