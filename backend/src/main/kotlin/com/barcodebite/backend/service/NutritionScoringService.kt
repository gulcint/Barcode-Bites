package com.barcodebite.backend.service

import com.barcodebite.backend.model.NutritionValues

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
}
