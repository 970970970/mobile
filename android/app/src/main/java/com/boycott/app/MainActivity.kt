class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BoycottTheme {
                MainTabView()
            }
        }
    }
}

@Composable
fun MainTabView() {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedBrand by remember { mutableStateOf<Brand?>(null) }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                BottomNavigationItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Filled.Home, "首页") },
                    label = { Text("首页") }
                )
                BottomNavigationItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Filled.List, "品牌") },
                    label = { Text("品牌") }
                )
                // 中间的扫描按钮
                Box(
                    modifier = Modifier
                        .offset(y = (-40).dp)
                        .size(72.dp)
                ) {
                    FloatingActionButton(
                        onClick = { selectedTab = 2 },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.QrCodeScanner, "扫描")
                            Text(
                                "扫描",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
                BottomNavigationItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Filled.Article, "文章") },
                    label = { Text("文章") }
                )
                BottomNavigationItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Filled.Settings, "设置") },
                    label = { Text("设置") }
                )
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> HomeView(paddingValues)
            1 -> BrandListView(paddingValues)
            2 -> ScanView(paddingValues)
            3 -> ArticleListView(paddingValues)
            4 -> SettingsView(paddingValues)
        }
    }
    
    // 品牌详情弹窗
    selectedBrand?.let { brand ->
        BrandDetailSheet(
            brand = brand,
            onDismiss = { selectedBrand = null }
        )
    }
} 