package com.barcodebite.backend.model

import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(
    val status: String,
)

@Serializable
data class ErrorResponse(
    val message: String,
)
