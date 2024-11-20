package com.boycott.app

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.boycott.app.ui.home.HomeView
import com.boycott.app.ui.settings.SettingsView
import com.boycott.app.ui.settings.PrivacyPolicyView
import com.boycott.app.ui.settings.UserAgreementView
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.ui.tooling.preview.Preview
import java.util.Locale
import com.boycott.app.utils.LocaleEvent
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import android.view.View
import com.boycott.app.utils.ThemeEvent
import com.boycott.app.ui.articles.ArticleListView
import com.boycott.app.ui.articles.ArticleDetailView

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        val savedLanguageCode = newBase.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getString("language_code", null)
        
        if (savedLanguageCode != null) {
            val locale = when (savedLanguageCode) {
                "zh-CN" -> Locale.CHINESE
                "en-US" -> Locale.US
                "hi-IN" -> Locale("hi", "IN")
                "es-ES" -> Locale("es", "ES")
                // ... 添加其他语言
                else -> Locale.getDefault()
            }
            
            val config = Configuration(newBase.resources.configuration)
            config.setLocale(locale)
            val context = newBase.createConfigurationContext(config)
            super.attachBaseContext(context)
        } else {
            super.attachBaseContext(newBase)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var forceUpdate by remember { mutableStateOf(0) }
            val scope = rememberCoroutineScope()
            val navController = rememberNavController()
            
            // 获取深色模式状态
            var isDarkMode by remember { 
                mutableStateOf(
                    getSharedPreferences("settings", Context.MODE_PRIVATE)
                        .getBoolean("dark_mode", false)
                )
            }
            
            // 监听语言变更
            LaunchedEffect(Unit) {
                LocaleEvent.localeChanged.collect { languageCode ->
                    scope.launch {
                        updateLocale(languageCode)
                        forceUpdate++
                    }
                }
            }
            
            // 监听主题变更
            LaunchedEffect(Unit) {
                ThemeEvent.themeChanged.collect { darkMode ->
                    isDarkMode = darkMode
                }
            }
            
            MaterialTheme(
                colorScheme = if (isDarkMode) {
                    darkColorScheme()
                } else {
                    lightColorScheme()
                }
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    key(forceUpdate) {
                        var selectedTab by remember { mutableStateOf(0) }  // 默认选中首页
                        
                        Scaffold(
                            bottomBar = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                ) {
                                    // 底部导航栏背景
                                    Surface(
                                        modifier = Modifier.fillMaxSize(),
                                        shadowElevation = 8.dp
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalArrangement = Arrangement.SpaceAround,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // 首页
                                            TabItem(
                                                icon = Icons.Filled.Home,
                                                label = stringResource(R.string.nav_home),
                                                selected = selectedTab == 0,
                                                onClick = { 
                                                    selectedTab = 0
                                                    navController.navigate("home") {
                                                        popUpTo("home") { inclusive = true }
                                                    }
                                                }
                                            )
                                            
                                            // 品牌
                                            TabItem(
                                                icon = Icons.AutoMirrored.Filled.List,
                                                label = stringResource(R.string.nav_brands),
                                                selected = selectedTab == 1,
                                                onClick = { 
                                                    selectedTab = 1
                                                    navController.navigate("brands") {
                                                        popUpTo("home")
                                                    }
                                                }
                                            )
                                            
                                            // 扫描按钮占位
                                            Spacer(modifier = Modifier.width(80.dp))
                                            
                                            // 文章
                                            TabItem(
                                                icon = Icons.AutoMirrored.Filled.MenuBook,
                                                label = stringResource(R.string.nav_articles),
                                                selected = selectedTab == 3,
                                                onClick = { 
                                                    selectedTab = 3
                                                    navController.navigate("articles") {
                                                        popUpTo("home")
                                                    }
                                                }
                                            )
                                            
                                            // 设置
                                            TabItem(
                                                icon = Icons.Filled.Settings,
                                                label = stringResource(R.string.nav_settings),
                                                selected = selectedTab == 4,
                                                onClick = { 
                                                    selectedTab = 4
                                                    navController.navigate("settings") {
                                                        popUpTo("home")
                                                    }
                                                }
                                            )
                                        }
                                    }
                                    
                                    // 凸起的扫描按钮
                                    FloatingActionButton(
                                        onClick = { 
                                            selectedTab = 2
                                            navController.navigate("scan") {
                                                popUpTo("home")
                                            }
                                        },
                                        modifier = Modifier
                                            .size(64.dp)
                                            .align(Alignment.TopCenter)
                                            .offset(y = (-20).dp),
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.PhotoCamera,
                                                contentDescription = stringResource(R.string.nav_scan),
                                                tint = Color.White,
                                                modifier = Modifier.size(28.dp)
                                            )
                                            Text(
                                                stringResource(R.string.nav_scan),
                                                color = Color.White,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        ) { paddingValues ->
                            Box(modifier = Modifier.padding(paddingValues)) {
                                NavHost(navController = navController, startDestination = "home") {
                                    composable("home") { 
                                        HomeView(onBrandClick = { _ -> /* 处理品牌点击 */ })
                                    }
                                    composable("brands") { Text("品牌列表") }
                                    composable("scan") { Text("扫描") }
                                    composable("articles") { 
                                        ArticleListView(
                                            onArticleClick = { articleId ->
                                                navController.navigate("article_detail/$articleId")
                                            }
                                        )
                                    }
                                    composable("settings") { 
                                        SettingsView(
                                            onNavigateToPrivacyPolicy = { navController.navigate("privacy_policy") },
                                            onNavigateToUserAgreement = { navController.navigate("user_agreement") }
                                        )
                                    }
                                    composable("privacy_policy") { 
                                        PrivacyPolicyView(onBack = { navController.popBackStack() })
                                    }
                                    composable("user_agreement") { 
                                        UserAgreementView(onBack = { navController.popBackStack() })
                                    }
                                    composable(
                                        "article_detail/{articleId}",
                                        arguments = listOf(navArgument("articleId") { type = NavType.IntType })
                                    ) { backStackEntry ->
                                        ArticleDetailView(
                                            onBack = { navController.popBackStack() }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateLocale(languageCode: String) {
        val locale = when (languageCode) {
            "zh-CN" -> Locale.CHINESE
            "en-US" -> Locale.US
            "hi-IN" -> Locale("hi", "IN")
            "es-ES" -> Locale("es", "ES")
            else -> Locale.getDefault()
        }
        
        val config = resources.configuration
        config.setLocale(locale)
        createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
        
        // 只触发重组，不重新调用 setContent
        window.decorView.findViewById<View>(android.R.id.content)?.let { view ->
            view.invalidate()
        }
    }
}

@Composable
private fun TabItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) MaterialTheme.colorScheme.primary else Color.Gray,
            modifier = Modifier.size(if (selected) 24.dp else 20.dp)
        )
        Text(
            text = label,
            color = if (selected) MaterialTheme.colorScheme.primary else Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TabItem(
                        icon = Icons.Filled.Home,
                        label = "首页",
                        selected = true,
                        onClick = {}
                    )
                    
                    TabItem(
                        icon = Icons.AutoMirrored.Filled.List,
                        label = "品牌",
                        selected = false,
                        onClick = {}
                    )
                    
                    Spacer(modifier = Modifier.width(80.dp))
                    
                    TabItem(
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        label = "文章",
                        selected = false,
                        onClick = {}
                    )
                    
                    TabItem(
                        icon = Icons.Filled.Settings,
                        label = "设置",
                        selected = false,
                        onClick = {}
                    )
                }
            }
            
            FloatingActionButton(
                onClick = {},
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (-20).dp),
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = "扫描",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        "扫描",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
} 