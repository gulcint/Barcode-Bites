package com.barcodebite.shared.domain.model

data class UserProfile(
    val id: Int,
    val email: String,
    val displayName: String,
    val createdAtEpochMs: Long,
)
