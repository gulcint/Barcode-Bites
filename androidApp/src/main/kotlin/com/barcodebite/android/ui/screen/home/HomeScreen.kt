package com.barcodebite.android.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onScanClick: () -> Unit,
    onHistoryClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Barcode Bites",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "Barkod tarat, ürün analizini gör.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onScanClick) {
                Text("Tarayıcıyı Aç")
            }
            Button(onClick = onHistoryClick) {
                Text("Geçmiş")
            }
        }
    }
}
