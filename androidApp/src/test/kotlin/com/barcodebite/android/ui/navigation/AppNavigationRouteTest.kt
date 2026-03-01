package com.barcodebite.android.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class AppNavigationRouteTest {
    @Test
    fun resolveAdvancedAnalysisRoute_nonPremium_goesToPaywall() {
        assertEquals(AppRoute.Paywall, resolveAdvancedAnalysisRoute(isPremium = false))
    }

    @Test
    fun resolveAdvancedAnalysisRoute_premium_goesToCompare() {
        assertEquals(AppRoute.Compare, resolveAdvancedAnalysisRoute(isPremium = true))
    }
}
