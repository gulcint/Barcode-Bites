package com.barcodebite.shared.domain.model

data class NutritionScore(
    val score: Int,
    val grade: String,
    val reason: String,
    val cleanLabelScore: Int = 0,
    val cleanLabelVerdict: String = "Unknown",
)
