package com.barcodebite.shared.data.local.history

import com.barcodebite.shared.domain.model.Nutrition
import com.barcodebite.shared.domain.model.NutritionScore
import com.barcodebite.shared.domain.model.Product
import com.barcodebite.shared.domain.model.ScanResult
import com.barcodebite.shared.domain.repository.ScanHistoryRepository

class SqlDelightScanHistoryRepository(
    private val localDataSource: ScanHistoryLocalDataSource,
) : ScanHistoryRepository {
    override suspend fun getRecent(limit: Int): List<ScanResult> {
        return localDataSource.selectRecent(limit = limit.toLong()).map { item ->
            ScanResult(
                product = Product(
                    barcode = item.barcode,
                    name = item.name,
                    brand = item.brand,
                    nutrition = Nutrition(
                        calories = 0.0,
                        protein = 0.0,
                        carbohydrates = 0.0,
                        fat = 0.0,
                        sugar = 0.0,
                        salt = 0.0,
                    ),
                ),
                nutritionScore = NutritionScore(
                    score = item.score.toInt(),
                    grade = item.grade,
                    reason = "Lokal tarama geçmişi",
                    cleanLabelScore = item.clean_label_score.toInt(),
                    cleanLabelVerdict = if (item.clean_label_score >= 70L) "Good" else "Average",
                    isJunkFood = item.is_junk_food == 1L,
                ),
                scannedAtEpochMs = item.scanned_at_epoch_ms,
            )
        }
    }

    override suspend fun save(result: ScanResult) {
        localDataSource.insertOrReplace(
            id = "${result.product.barcode}-${result.scannedAtEpochMs}",
            barcode = result.product.barcode,
            name = result.product.name,
            brand = result.product.brand,
            grade = result.nutritionScore.grade,
            score = result.nutritionScore.score.toLong(),
            cleanLabelScore = result.nutritionScore.cleanLabelScore.toLong(),
            isJunkFood = if (result.nutritionScore.isJunkFood) 1L else 0L,
            scannedAtEpochMs = result.scannedAtEpochMs,
        )

        val count = localDataSource.countAll()
        if (count > MAX_HISTORY_SIZE) {
            localDataSource.deleteOverflow(keepCount = MAX_HISTORY_SIZE)
        }
    }

    override suspend fun clear() {
        localDataSource.deleteAll()
    }

    private companion object {
        const val MAX_HISTORY_SIZE = 20L
    }
}
