package com.barcodebite.shared.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class VerifySubscriptionRequestDto(
    val plan: String,
    val purchaseToken: String,
)

@Serializable
data class RestoreSubscriptionRequestDto(
    val purchaseToken: String,
)
