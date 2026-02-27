package com.barcodebite.android.ui.screen.scanner

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
fun ScannerScreen(
    onBarcodeDetected: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Tarayıcı",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "CameraX + ML Kit entegrasyonu bir sonraki adımda eklenecek.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
        )
        Button(onClick = onBarcodeDetected) {
            Text("Sahte Barkod Okut")
        }
        Button(
            onClick = onBack,
            modifier = Modifier.padding(top = 12.dp),
        ) {
            Text("Geri")
        }
    }
}
