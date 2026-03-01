package com.barcodebite.android.ui.screen.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    onGoPremium: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

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
            text = "Profil",
            style = MaterialTheme.typography.headlineMedium,
        )

        when (val state = uiState) {
            ProfileUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            }

            is ProfileUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }

            is ProfileUiState.Success -> {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 16.dp)) {
                        Text(text = "Ad: ${state.data.profile.displayName}", style = MaterialTheme.typography.titleMedium)
                        Text(text = "E-posta: ${state.data.profile.email}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Plan: ${state.data.subscription.plan}", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = if (state.data.subscription.isActive) "Premium aktif" else "Free plan",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.padding(top = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(onClick = onBack) {
                Text("Geri")
            }
            Button(onClick = onGoPremium) {
                Text("Premium'a Gec")
            }
        }
    }
}
