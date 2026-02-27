package com.barcodebite.android.ui.screen.scanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun ScannerScreen(
    onBarcodeDetected: (String) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    var cameraErrorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var detectedBarcode by rememberSaveable { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Barkod Tarayıcı",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
        )

        if (hasCameraPermission) {
            CameraPreview(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
                onBarcodeDetected = { barcode ->
                    if (detectedBarcode == null) {
                        detectedBarcode = barcode
                        onBarcodeDetected(barcode)
                    }
                },
                onError = { message ->
                    cameraErrorMessage = message
                },
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Tarayıcı için kamera izni gerekli.",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    modifier = Modifier.padding(top = 12.dp),
                ) {
                    Text("Kamera İzni Ver")
                }
            }
        }

        if (cameraErrorMessage != null) {
            Text(
                text = cameraErrorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp),
            )
        }

        if (detectedBarcode != null) {
            Text(
                text = "Algılanan barkod: $detectedBarcode",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 12.dp),
            )
        }

        Button(
            onClick = { onBarcodeDetected("8690504012345") },
            modifier = Modifier.padding(top = 12.dp),
        ) {
            Text("Örnek Barkodla Devam Et")
        }

        Button(
            onClick = onBack,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
        ) {
            Text("Geri")
        }
    }
}
