package com.barcodebite.shared.data.remote.dto

import com.barcodebite.shared.domain.model.UserProfile
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val id: Int,
    val email: String,
    val displayName: String,
    val createdAtEpochMs: Long,
)

fun UserProfileDto.toDomain(): UserProfile = UserProfile(
    id = id,
    email = email,
    displayName = displayName,
    createdAtEpochMs = createdAtEpochMs,
)
