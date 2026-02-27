package com.barcodebite.backend.service

import com.barcodebite.backend.model.NutritionValues

data class JunkFoodAssessment(
    val isJunkFood: Boolean,
    val reasons: List<String>,
)

class NutritionScoringService {
    fun calculateScore(nutrition: NutritionValues): Int {
        val penalty = (nutrition.sugar * 2.5) + (nutrition.salt * 20.0) + (nutrition.calories * 0.04)
        val bonus = nutrition.protein * 1.2
        val raw = 100.0 - penalty + bonus
        return raw.toInt().coerceIn(0, 100)
    }

    fun grade(score: Int): String {
        return when {
            score >= 85 -> "A"
            score >= 70 -> "B"
            score >= 55 -> "C"
            score >= 40 -> "D"
            else -> "E"
        }
    }

    fun calculateCleanLabelScore(
        nutrition: NutritionValues,
        ingredients: String,
        additives: List<String>,
    ): Int {
        val normalizedAdditives = additives
            .map { it.trim().lowercase() }
            .filter { it.isNotBlank() }

        val riskyCount = normalizedAdditives.count { it in riskyAdditives }
        val additivePenalty = normalizedAdditives.size * 8.0
        val riskyPenalty = riskyCount * 6.0
        val sugarPenalty = (nutrition.sugar * 1.5).coerceAtMost(25.0)
        val saltPenalty = (nutrition.salt * 8.0).coerceAtMost(20.0)
        val ingredientCount = ingredients
            .split(',')
            .map { it.trim() }
            .count { it.isNotBlank() }
        val ingredientComplexityPenalty = ((ingredientCount - 5).coerceAtLeast(0) * 3.0).coerceAtMost(25.0)

        val rawScore = 100.0 - additivePenalty - riskyPenalty - sugarPenalty - saltPenalty - ingredientComplexityPenalty
        return rawScore.toInt().coerceIn(0, 100)
    }

    fun cleanLabelVerdict(score: Int): String {
        return when {
            score >= 85 -> "Excellent"
            score >= 70 -> "Good"
            score >= 55 -> "Fair"
            score >= 40 -> "Poor"
            else -> "Ultra-Processed"
        }
    }

    fun detectJunkFood(
        nutrition: NutritionValues,
        cleanLabelScore: Int,
        additives: List<String>,
    ): JunkFoodAssessment {
        val normalizedAdditives = additives
            .map { it.trim().lowercase() }
            .filter { it.isNotBlank() }

        val reasons = mutableListOf<String>()

        if (nutrition.sugar >= 20.0) {
            reasons += "High sugar content"
        }
        if (nutrition.salt >= 1.0) {
            reasons += "High salt content"
        }
        if (nutrition.calories >= 400.0) {
            reasons += "High calorie density"
        }
        if (cleanLabelScore < 45) {
            reasons += "Low clean label score"
        }
        if (normalizedAdditives.size >= 3) {
            reasons += "Multiple additives"
        }
        if (normalizedAdditives.any { it in riskyAdditives }) {
            reasons += "Contains risky additives"
        }

        val isJunk = reasons.size >= 2
        return JunkFoodAssessment(
            isJunkFood = isJunk,
            reasons = if (isJunk) reasons.distinct() else emptyList(),
        )
    }

    private companion object {
        val riskyAdditives = setOf(
            "e102",
            "e110",
            "e124",
            "e129",
            "e211",
            "e220",
            "e621",
            "e950",
            "e951",
        )
    }
}
