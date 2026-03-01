package com.barcodebite.android.ui.screen.paywall

import com.barcodebite.android.data.profile.SubscriptionSyncRepository
import com.barcodebite.android.premium.PremiumGate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed interface PaywallUiState {
    data object Idle : PaywallUiState
    data object Purchasing : PaywallUiState
    data class Success(val plan: String) : PaywallUiState
    data class Error(val message: String) : PaywallUiState
}

class PaywallViewModel(
    private val premiumGate: PremiumGate,
    private val subscriptionSyncRepository: SubscriptionSyncRepository? = null,
    private val accessTokenProvider: () -> String? = { null },
) {
    private val _uiState = MutableStateFlow<PaywallUiState>(PaywallUiState.Idle)
    val uiState: StateFlow<PaywallUiState> = _uiState.asStateFlow()

    fun purchase(plan: String) {
        if (plan.isBlank()) {
            _uiState.update { PaywallUiState.Error("Gecersiz plan") }
            return
        }

        _uiState.update { PaywallUiState.Purchasing }
        runCatching {
            premiumGate.enablePremium(plan)
        }.onSuccess {
            _uiState.update { PaywallUiState.Success(plan) }
        }.onFailure {
            _uiState.update { PaywallUiState.Error("Satin alma basarisiz") }
        }
    }

    fun restore() {
        if (premiumGate.isPremiumEnabled()) {
            _uiState.update { PaywallUiState.Success("premium_restored") }
        } else {
            _uiState.update { PaywallUiState.Error("Geri yuklenecek satin alma bulunamadi") }
        }
    }

    suspend fun onExternalPurchase(
        plan: String,
        purchaseToken: String,
    ) {
        if (plan.isBlank()) {
            _uiState.update { PaywallUiState.Error("Gecersiz plan") }
            return
        }
        premiumGate.enablePremium(plan)
        runCatching {
            subscriptionSyncRepository?.verify(
                plan = plan,
                purchaseToken = purchaseToken,
                accessToken = accessTokenProvider(),
            )
        }
        _uiState.update { PaywallUiState.Success(plan) }
    }

    suspend fun onExternalRestore(purchaseToken: String) {
        runCatching {
            subscriptionSyncRepository?.restore(
                purchaseToken = purchaseToken,
                accessToken = accessTokenProvider(),
            )
        }.onSuccess { status ->
            if (status != null && status.isActive) {
                premiumGate.enablePremium(status.plan)
                _uiState.update { PaywallUiState.Success(status.plan) }
                return
            }
            if (premiumGate.isPremiumEnabled()) {
                _uiState.update { PaywallUiState.Success("premium_restored") }
                return
            }
            _uiState.update { PaywallUiState.Error("Geri yuklenecek satin alma bulunamadi") }
        }.onFailure {
            if (premiumGate.isPremiumEnabled()) {
                _uiState.update { PaywallUiState.Success("premium_restored") }
            } else {
                _uiState.update { PaywallUiState.Error("Geri yukleme basarisiz") }
            }
        }
    }

    fun onExternalError(message: String) {
        _uiState.update { PaywallUiState.Error(message.ifBlank { "Satin alma basarisiz" }) }
    }

    fun retry() {
        _uiState.update { PaywallUiState.Idle }
    }
}
