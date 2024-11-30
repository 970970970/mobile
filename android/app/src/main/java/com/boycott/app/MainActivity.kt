package com.boycott.app

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.boycott.app.ui.articles.ArticleDetailView
import com.boycott.app.ui.articles.ArticleListView
import com.boycott.app.ui.brands.BrandsView
import com.boycott.app.ui.home.HomeView
import com.boycott.app.ui.settings.PrivacyPolicyView
import com.boycott.app.ui.settings.SettingsView
import com.boycott.app.ui.settings.UserAgreementView
import com.boycott.app.ui.search.SearchHistoryView
import com.boycott.app.ui.search.SearchResultsView
import com.boycott.app.utils.LocaleEvent
import com.boycott.app.utils.ThemeEvent
import com.boycott.app.ui.brand.BrandDetailView
import com.boycott.app.ui.home.HomeViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.lifecycleScope
import androidx.compose.ui.layout.ContentScale
import com.boycott.app.ui.camera.CameraView
import androidx.compose.material.icons.filled.Image
import androidx.activity.result.contract.ActivityResultContracts

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // 添加图片选择器
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            // 暂时只打印日志
            Log.d("MainActivity", "Selected image: $it")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var forceUpdate by remember { mutableStateOf(0) }
            val navController = rememberNavController()
            
            // 获取深色模式状态
            var isDarkMode by remember { 
                mutableStateOf(
                    getSharedPreferences("settings", Context.MODE_PRIVATE)
                        .getBoolean("dark_mode", false)
                )
            }
            
            MaterialTheme(
                colorScheme = if (isDarkMode) {
                    darkColorScheme()
                } else {
                    lightColorScheme()
                }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // 原有的主界面内容
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        key(forceUpdate) {
                            var selectedTab by remember { mutableStateOf(0) }
                            
                            // 监听导航变化
                            LaunchedEffect(navController) {
                                navController.currentBackStackEntryFlow.collect { _ ->
                                    selectedTab = when(navController.currentDestination?.route) {
                                        "home" -> 0
                                        "brands" -> 1
                                        "scan" -> 2
                                        "articles" -> 3
                                        "settings" -> 4
                                        else -> selectedTab
                                    }
                                }
                            }
                            
                            Scaffold(
                                floatingActionButton = {
                                    FloatingActionButton(
                                        onClick = { 
                                            selectedTab = 2
                                            navController.navigate("scan") {
                                                popUpTo("home")
                                            }
                                        },
                                        modifier = Modifier
                                            .size(64.dp)
                                            .offset(y = (56.dp)),
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
                                },
                                floatingActionButtonPosition = FabPosition.Center,
                                bottomBar = {
                                    NavigationBar {
                                        Box(modifier = Modifier.fillMaxSize()) {
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
                                                    
                                                    // 品
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
                                        }
                                    }
                                }
                            ) { paddingValues ->
                                Box(modifier = Modifier.padding(paddingValues)) {
                                    NavHost(navController = navController, startDestination = "home") {
                                        composable("home") { 
                                            HomeView(
                                                onBrandClick = { brandId -> 
                                                    navController.navigate("brand/$brandId")
                                                },
                                                onArticleClick = { articleId ->
                                                    navController.navigate("article_detail/$articleId")
                                                },
                                                onNavigateToSearchHistory = {
                                                    navController.navigate("search_history")
                                                },
                                                onNavigateToSearchResults = { query -> 
                                                    navController.navigate("search_results/$query")
                                                }
                                            )
                                        }
                                        composable("brands") { 
                                            BrandsView(
                                                onNavigateToSearchHistory = {
                                                    navController.navigate("search_history")
                                                },
                                                onBrandClick = { brandId -> 
                                                    navController.navigate("brand/$brandId")
                                                },
                                                onNavigateToSearchResults = { query -> 
                                                    navController.navigate("search_results/$query")
                                                }
                                            )
                                        }
                                        composable("scan") {
                                            CameraView(
                                                onNavigateBack = { navController.navigateUp() }
                                            )
                                        }
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
                                        ) { _ ->
                                            ArticleDetailView(
                                                onBack = { navController.popBackStack() }
                                            )
                                        }

                                        // 添加搜索相关的路由
                                        composable("search_history") { 
                                            val parentEntry = remember { navController.getBackStackEntry("home") }
                                            val parentHomeViewModel = hiltViewModel<HomeViewModel>(parentEntry)
                                            val searchText = parentHomeViewModel.searchText.collectAsState().value
                                            
                                            SearchHistoryView(
                                                initialQuery = searchText,
                                                onSearch = { query ->
                                                    navController.navigate("search_results/$query")
                                                },
                                                onBack = {
                                                    navController.popBackStack()
                                                },
                                                onBrandClick = { brandId -> 
                                                    navController.navigate("brand/$brandId")
                                                }
                                            )
                                        }
                                        
                                        composable(
                                            "search_results/{query}",
                                            arguments = listOf(navArgument("query") { type = NavType.StringType })
                                        ) { backStackEntry ->
                                            val query = backStackEntry.arguments?.getString("query") ?: return@composable
                                            SearchResultsView(
                                                query = query,
                                                onBack = { navController.popBackStack() },
                                                onBrandClick = { brandId -> navController.navigate("brand/$brandId") }
                                            )
                                        }

                                        // 添加品牌详情页的路由
                                        composable(
                                            "brand/{brandId}",
                                            arguments = listOf(navArgument("brandId") { type = NavType.StringType })
                                        ) { backStackEntry ->
                                            val brandId = backStackEntry.arguments?.getString("brandId") ?: return@composable
                                            BrandDetailView(
                                                brandId = brandId,
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