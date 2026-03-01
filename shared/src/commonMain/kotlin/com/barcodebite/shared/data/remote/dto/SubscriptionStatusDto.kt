package com.barcodebite.shared.data.remote.dto

import com.barcodebite.shared.domain.model.PremiumEntitlements
import com.barcodebite.shared.domain.model.SubscriptionStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionEntitlementsDto(
    val compare: Boolean = false,
    @SerialName("advanced_analysis")
    val advancedAnalysis: Boolean = false,
    @SerialName("unlimited_scans")
    val unlimitedScans: Boolean = false,
)

@Serializable
data class SubscriptionStatusDto(
    val plan: String,
    val isActive: Boolean,
    val expiresAtEpochMs: Long? = null,
    val entitlements: SubscriptionEntitlementsDto = SubscriptionEntitlementsDto(),
)

fun SubscriptionStatusDto.toDomain(): SubscriptionStatus = SubscriptionStatus(
    plan = plan,
    isActive = isActive,
    expiresAtEpochMs = expiresAtEpochMs,
    entitlements = PremiumEntitlements(
        compare = entitlements.compare,
        advancedAnalysis = entitlements.advancedAnalysis,
        unlimitedScans = entitlements.unlimitedScans,
    ),
)
