package com.barcodebite.shared.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NutritionDto(
    val calories: Double,
    val protein: Double,
    val carbohydrates: Double,
    val fat: Double,
    val sugar: Double,
    val salt: Double,
)
