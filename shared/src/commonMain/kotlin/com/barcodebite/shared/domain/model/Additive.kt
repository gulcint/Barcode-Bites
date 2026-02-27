package com.barcodebite.shared.domain.model

data class Additive(
    val code: String,
    val name: String,
    val riskLevel: String,
    val description: String? = null,
)
