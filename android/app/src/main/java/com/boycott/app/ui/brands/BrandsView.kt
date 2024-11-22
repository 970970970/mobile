package com.boycott.app.ui.brands

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.boycott.app.utils.AppConfig
import com.boycott.app.ui.components.SearchBar
import com.boycott.app.data.model.Brand

@Composable
fun BrandsView(
    onNavigateToSearchHistory: () -> Unit,
    viewModel: BrandsViewModel = hiltViewModel()
) {
    val currentHotSearch by viewModel.currentHotSearch.collectAsState()
    val brands by viewModel.brands.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            hotSearchText = currentHotSearch,
            onSearchClick = onNavigateToSearchHistory
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(brands) { brand ->
                BrandItem(brand = brand)
            }
        }
    }
}

@Composable
private fun BrandItem(brand: Brand) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (brand.logo_path != null) {
                    AsyncImage(
                        model = "http://10.1.0.241:8787/v1/media/local/${brand.logo_path}",
                        contentDescription = brand.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = brand.name.firstOrNull()?.toString() ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = brand.name,
                    style = MaterialTheme.typography.titleMedium
                )
                
                brand.status?.let { status ->
                    Text(
                        text = when(status) {
                            "avoid" -> "抵制"
                            "support" -> "支持"
                            "neutral" -> "中立"
                            else -> status
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = when(status) {
                            "avoid" -> MaterialTheme.colorScheme.error
                            "support" -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
} 