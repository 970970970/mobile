package com.boycott.app.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.boycott.app.R
import com.jeziellago.compose.markdowntext.MarkdownText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val donationText = """
        # 关于捐赠

        感谢您愿意支持我们的工作。不过，我们建议您将捐赠用于更有意义的地方：

        ## 推荐的捐赠对象

        - [Palestine Children's Relief Fund](https://www.pcrf.net/)
        - [Medical Aid for Palestinians](https://www.map.org.uk/)
        - [UNRWA](https://www.unrwa.org/)
        
        这些组织长期致力于为受影响地区的民众提供援助。您的捐赠将直接帮助到需要帮助的人们。

        ## 为什么我们不接受捐赠？

        我们是一个完全独立的项目，运营成本很低。我们希望保持独立性，不受任何经济因素的影响。

        ## 其他支持方式

        如果您想支持我们的工作，可以：

        1. 在日常生活中参考我们的信息，做出明智的消费选择
        2. 向身边的人分享这个应用
        3. 如果您是开发者，欢迎在 GitHub 上贡献代码
        4. 为数据库提供更多准确的信息

        感谢您的支持！
    """.trimIndent()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.support_us)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MarkdownText(
                markdown = donationText,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.onSurface,
                linkColor = MaterialTheme.colorScheme.primary
            )
        }
    }
} 