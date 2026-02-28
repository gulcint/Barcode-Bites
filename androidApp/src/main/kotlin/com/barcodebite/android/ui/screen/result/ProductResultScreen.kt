package com.barcodebite.android.ui.screen.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.barcodebite.android.BuildConfig
import com.barcodebite.android.data.ProductResultData
import com.barcodebite.android.data.ProductResultRepository
import com.barcodebite.shared.domain.repository.ScanHistoryRepository

private sealed interface ProductResultUiState {
    data object Loading : ProductResultUiState
    data class Success(val data: ProductResultData) : ProductResultUiState
    data class Error(val message: String) : ProductResultUiState
}

@Composable
fun ProductResultScreen(
    barcode: String,
    scanHistoryRepository: ScanHistoryRepository,
    onBackToHome: () -> Unit,
) {
    val repository = remember {
        ProductResultRepository(
            baseUrl = BuildConfig.API_BASE_URL,
            scanHistoryRepository = scanHistoryRepository,
        )
    }
    var reloadKey by remember { mutableIntStateOf(0) }
    var uiState by remember(barcode, reloadKey) {
        mutableStateOf<ProductResultUiState>(ProductResultUiState.Loading)
    }

    DisposableEffect(Unit) {
        onDispose {
            repository.close()
        }
    }

    LaunchedEffect(barcode, reloadKey) {
        if (barcode.isBlank()) {
            uiState = ProductResultUiState.Error("Barkod bulunamadı")
            return@LaunchedEffect
        }

        uiState = ProductResultUiState.Loading

        uiState = runCatching { repository.fetch(barcode) }
            .fold(
                onSuccess = { ProductResultUiState.Success(it) },
                onFailure = {
                    ProductResultUiState.Error(
                        message = "Ürün bilgisi alınamadı. Backend çalışıyor mu?",
                    )
                },
            )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = "Ürün Sonucu",
            style = MaterialTheme.typography.headlineMedium,
        )

        when (val state = uiState) {
            ProductResultUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                Text(
                    text = "Ürün analizi alınıyor...",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }

            is ProductResultUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp),
                )
                Button(
                    onClick = { reloadKey += 1 },
                    modifier = Modifier.padding(top = 12.dp),
                ) {
                    Text("Tekrar Dene")
                }
            }

            is ProductResultUiState.Success -> {
                val data = state.data
                Text(
                    text = "Barkod: ${data.product.barcode}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Text(
                    text = data.product.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = data.product.brand,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = "Skor: ${data.score.score} (${data.score.grade})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp),
                )
                Text(
                    text = data.score.reason,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 6.dp),
                )
                Text(
                    text = "Clean Label: ${data.score.cleanLabelScore}/100 (${data.score.cleanLabelVerdict})",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 10.dp),
                )
                if (data.score.isJunkFood) {
                    Text(
                        text = "Junk Food Uyarisi",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 10.dp),
                    )
                    data.score.junkFoodReasons.forEach { reason ->
                        Text(
                            text = "- $reason",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }
                NutritionChart(
                    nutrition = data.product.nutrition,
                    grade = data.score.grade,
                    modifier = Modifier.padding(top = 20.dp),
                )
                AdditivesList(
                    additives = data.product.additives,
                    modifier = Modifier.padding(top = 20.dp),
                )
                Text(
                    text = "Kalori: ${data.product.nutrition.calories} kcal | Şeker: ${data.product.nutrition.sugar} g | Tuz: ${data.product.nutrition.salt} g",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 14.dp),
                )
            }
        }

        Button(
            onClick = onBackToHome,
            modifier = Modifier.padding(top = 24.dp),
        ) {
            Text("Ana Sayfaya Dön")
        }
    }
}
