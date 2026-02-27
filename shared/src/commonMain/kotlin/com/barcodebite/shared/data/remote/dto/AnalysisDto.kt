package com.barcodebite.shared.data.remote.dto

import com.barcodebite.shared.domain.model.NutritionScore
import kotlinx.serialization.Serializable

@Serializable
data class AnalysisDto(
    val barcode: String,
    val score: Int,
    val grade: String,
    val summary: String,
    val cleanLabelScore: Int = 0,
    val cleanLabelVerdict: String = "Unknown",
    val isJunkFood: Boolean = false,
    val junkFoodReasons: List<String> = emptyList(),
)

fun AnalysisDto.toDomain(): NutritionScore = NutritionScore(
    score = score,
    grade = grade,
    reason = summary,
    cleanLabelScore = cleanLabelScore,
    cleanLabelVerdict = cleanLabelVerdict,
    isJunkFood = isJunkFood,
    junkFoodReasons = junkFoodReasons,
)
