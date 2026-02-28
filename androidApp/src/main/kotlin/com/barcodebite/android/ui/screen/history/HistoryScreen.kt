package com.barcodebite.android.ui.screen.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onBack: () -> Unit,
    onOpenResult: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = "Tarama Geçmişi",
            style = MaterialTheme.typography.headlineMedium,
        )

        when (val state = uiState) {
            HistoryUiState.Loading -> {
                Text(
                    text = "Geçmiş yükleniyor...",
                    modifier = Modifier.padding(top = 16.dp),
                )
            }

            HistoryUiState.Empty -> {
                Text(
                    text = "Henüz tarama yok.",
                    modifier = Modifier.padding(top = 16.dp),
                )
            }

            is HistoryUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }

            is HistoryUiState.Content -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(state.items, key = { it.scannedAtEpochMs }) { item ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenResult(item.product.barcode) },
                        ) {
                            Text(
                                text = item.product.name,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = "${item.product.brand} • ${item.product.barcode}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                text = "Skor: ${item.nutritionScore.score} (${item.nutritionScore.grade})",
                                style = MaterialTheme.typography.bodySmall,
                            )
                            Text(
                                text = "Tarama: ${item.scannedAtEpochMs.toDateText()}",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.padding(top = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(onClick = onBack) {
                Text("Ana Sayfa")
            }
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.clear()
                    }
                },
            ) {
                Text("Temizle")
            }
        }
    }
}

private fun Long.toDateText(): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    return formatter.format(Date(this))
}
