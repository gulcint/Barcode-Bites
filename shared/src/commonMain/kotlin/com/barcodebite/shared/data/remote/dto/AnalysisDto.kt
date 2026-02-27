package com.barcodebite.shared.data.remote.dto

import com.barcodebite.shared.domain.model.NutritionScore
import kotlinx.serialization.Serializable

@Serializable
data class AnalysisDto(
    val barcode: String,
    val score: Int,
    val grade: String,
    val summary: String,
)

fun AnalysisDto.toDomain(): NutritionScore = NutritionScore(
    score = score,
    grade = grade,
    reason = summary,
)
