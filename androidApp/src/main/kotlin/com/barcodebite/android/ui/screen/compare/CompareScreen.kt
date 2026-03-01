package com.barcodebite.android.ui.screen.compare

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun CompareScreen(
    viewModel: CompareViewModel,
    onBack: () -> Unit,
) {
    var firstBarcode by remember { mutableStateOf("") }
    var secondBarcode by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Urun Karsilastirma", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = firstBarcode,
            onValueChange = { firstBarcode = it },
            label = { Text("1. Barkod") },
        )
        OutlinedTextField(
            value = secondBarcode,
            onValueChange = { secondBarcode = it },
            label = { Text("2. Barkod") },
        )

        Button(onClick = {
            scope.launch {
                viewModel.compare(firstBarcode, secondBarcode)
            }
        }) {
            Text("Karsilastir")
        }

        when (val state = uiState) {
            CompareUiState.Idle -> Text("Iki barkod girip karsilastirabilirsin.")
            CompareUiState.Loading -> CircularProgressIndicator()
            is CompareUiState.Error -> Text(
                text = state.message,
                color = MaterialTheme.colorScheme.error,
            )
            is CompareUiState.Success -> {
                Text("${state.data.firstBarcode}: ${state.data.firstScore} (${state.data.firstGrade})")
                Text("${state.data.secondBarcode}: ${state.data.secondScore} (${state.data.secondGrade})")
                Text("Oneri: ${state.data.recommendation}")
            }
        }

        Button(onClick = onBack) {
            Text("Geri")
        }
    }
}
