package com.barcodebite.backend.model

data class NutritionValues(
    val calories: Double,
    val protein: Double,
    val carbohydrates: Double,
    val fat: Double,
    val sugar: Double,
    val salt: Double,
) {
    companion object {
        fun empty(): NutritionValues = NutritionValues(
            calories = 0.0,
            protein = 0.0,
            carbohydrates = 0.0,
            fat = 0.0,
            sugar = 0.0,
            salt = 0.0,
        )
    }
}

data class ProductRecord(
    val barcode: String,
    val name: String,
    val brand: String,
    val nutrition: NutritionValues,
    val ingredients: String = "",
    val additives: List<String> = emptyList(),
)

data class UserRecord(
    val id: Int,
    val email: String,
    val passwordHash: String,
    val displayName: String,
    val createdAtEpochMs: Long,
)

data class SubscriptionRecord(
    val userId: Int,
    val plan: String,
    val isActive: Boolean,
    val expiresAtEpochMs: Long?,
)

data class AnalysisResult(
    val barcode: String,
    val score: Int,
    val grade: String,
    val cleanLabelScore: Int,
    val cleanLabelVerdict: String,
    val isJunkFood: Boolean,
    val junkFoodReasons: List<String>,
    val summary: String,
)

data class AnalysisComparisonResult(
    val first: AnalysisResult,
    val second: AnalysisResult,
    val recommendation: String,
)
