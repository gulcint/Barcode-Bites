package com.barcodebite.shared.domain.model

data class PremiumEntitlements(
    val compare: Boolean = false,
    val advancedAnalysis: Boolean = false,
    val unlimitedScans: Boolean = false,
)

data class SubscriptionStatus(
    val plan: String,
    val isActive: Boolean,
    val expiresAtEpochMs: Long? = null,
    val entitlements: PremiumEntitlements = PremiumEntitlements(),
)
