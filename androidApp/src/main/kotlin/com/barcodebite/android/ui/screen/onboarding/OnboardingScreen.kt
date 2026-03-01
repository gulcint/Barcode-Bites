package com.barcodebite.android.ui.screen.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(
    onContinue: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("BarcodeBite'a Hos Geldin", style = MaterialTheme.typography.headlineMedium)
        Text("1. Barkodu tara")
        Text("2. Besin degerlerini ve riskleri gor")
        Text("3. Premium ile urun karsilastirma ac")
        Button(onClick = onContinue) {
            Text("Basla")
        }
    }
}
