package com.barcodebite.android.ui.screen.paywall

import com.barcodebite.android.premium.PremiumGate
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PaywallViewModelTest {
    @Test
    fun purchaseMonthly_transitionsToSuccess() = runTest {
        val gate = object : PremiumGate {
            override fun isPremiumEnabled(): Boolean = false
            override fun enablePremium(plan: String) = Unit
            override fun disablePremium() = Unit
        }
        val viewModel = PaywallViewModel(premiumGate = gate)

        viewModel.purchase(plan = "premium_monthly")

        val state = viewModel.uiState.value
        assertTrue(state is PaywallUiState.Success)
        assertEquals("premium_monthly", (state as PaywallUiState.Success).plan)
    }

    @Test
    fun retry_fromError_returnsToIdle() {
        val gate = object : PremiumGate {
            override fun isPremiumEnabled(): Boolean = false
            override fun enablePremium(plan: String) = Unit
            override fun disablePremium() = Unit
        }
        val viewModel = PaywallViewModel(premiumGate = gate)

        viewModel.purchase(plan = "")
        viewModel.retry()

        assertEquals(PaywallUiState.Idle, viewModel.uiState.value)
    }
}
