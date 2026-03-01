package com.barcodebite.android.ui.screen.paywall

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.barcodebite.android.billing.PlayBillingGateway

@Composable
fun PaywallScreen(
    viewModel: PaywallViewModel,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val billingGateway = remember {
        PlayBillingGateway(
            context = context.applicationContext,
            onPremiumGranted = viewModel::onExternalPurchase,
            onError = viewModel::onExternalError,
        )
    }
    DisposableEffect(Unit) {
        onDispose { billingGateway.close() }
    }

    val pulse = rememberInfiniteTransition(label = "paywall-pulse")
    val titleAlpha by pulse.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "paywall-title-alpha",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = "Premium'a Gec",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.alpha(titleAlpha),
        )
        Text(
            text = "Sinirsiz tarama, karsilastirma ve gelismis analiz.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
        )

        Row(
            modifier = Modifier.padding(top = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(onClick = {
                val launched = billingGateway.launchMonthly(context.findActivity())
                if (!launched) viewModel.purchase("premium_monthly")
            }) {
                Text("Aylik")
            }
            Button(onClick = {
                val launched = billingGateway.launchYearly(context.findActivity())
                if (!launched) viewModel.purchase("premium_yearly")
            }) {
                Text("Yillik")
            }
            Button(onClick = {
                billingGateway.restorePurchases()
                viewModel.restore()
            }) {
                Text("Geri Yukle")
            }
        }

        AnimatedVisibility(visible = true, enter = fadeIn(), modifier = Modifier.padding(top = 16.dp)) {
            when (val current = state) {
                PaywallUiState.Idle -> Text("Plan secerek devam et.")
                PaywallUiState.Purchasing -> Text("Satin alma isleniyor...")
                is PaywallUiState.Success -> Text("Basarili: ${current.plan}")
                is PaywallUiState.Error -> Text(
                    text = current.message,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        Row(
            modifier = Modifier.padding(top = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(onClick = onBack) {
                Text("Geri")
            }
            Button(onClick = { viewModel.retry() }) {
                Text("Tekrar Dene")
            }
        }
    }
}

private fun android.content.Context.findActivity(): Activity {
    return when (this) {
        is Activity -> this
        is android.content.ContextWrapper -> baseContext.findActivity()
        else -> throw IllegalStateException("Activity not found in context chain")
    }
}
