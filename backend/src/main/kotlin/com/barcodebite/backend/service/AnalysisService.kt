package com.barcodebite.backend.service

import com.barcodebite.backend.model.AnalysisResult
import com.barcodebite.backend.model.NutritionValues
import com.barcodebite.backend.model.ProductRecord
import com.barcodebite.backend.repository.ProductRepository

class AnalysisService(
    private val productRepository: ProductRepository,
    private val nutritionScoringService: NutritionScoringService,
) {
    fun analyze(barcode: String): AnalysisResult {
        val product = productRepository.findByBarcode(barcode) ?: productRepository.upsert(
            ProductRecord(
                barcode = barcode,
                name = "Unknown Product",
                brand = "Unknown Brand",
                nutrition = NutritionValues.empty(),
                ingredients = "",
                additives = emptyList(),
            ),
        )

        val score = nutritionScoringService.calculateScore(product.nutrition)
        val grade = nutritionScoringService.grade(score)
        val cleanLabelScore = nutritionScoringService.calculateCleanLabelScore(
            nutrition = product.nutrition,
            ingredients = product.ingredients,
            additives = product.additives,
        )
        val cleanLabelVerdict = nutritionScoringService.cleanLabelVerdict(cleanLabelScore)
        val junkFoodAssessment = nutritionScoringService.detectJunkFood(
            nutrition = product.nutrition,
            cleanLabelScore = cleanLabelScore,
            additives = product.additives,
        )

        return AnalysisResult(
            barcode = barcode,
            score = score,
            grade = grade,
            cleanLabelScore = cleanLabelScore,
            cleanLabelVerdict = cleanLabelVerdict,
            isJunkFood = junkFoodAssessment.isJunkFood,
            junkFoodReasons = junkFoodAssessment.reasons,
            summary = "Calculated from stored nutrition profile.",
        )
    }
}
