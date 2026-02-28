package com.barcodebite.shared.data.remote.dto

import com.barcodebite.shared.domain.model.Additive
import kotlinx.serialization.Serializable

@Serializable
data class AdditiveDto(
    val code: String,
    val name: String,
    val riskLevel: String,
    val description: String,
)

fun AdditiveDto.toDomain(): Additive = Additive(
    code = code,
    name = name,
    riskLevel = riskLevel,
    description = description,
)
