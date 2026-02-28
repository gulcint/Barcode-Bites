package com.barcodebite.android.data

import com.barcodebite.shared.data.remote.HttpClientFactory
import com.barcodebite.shared.data.remote.api.BarcodeBiteApiService
import com.barcodebite.shared.data.remote.dto.toDomain
import com.barcodebite.shared.domain.model.NutritionScore
import com.barcodebite.shared.domain.model.Product
import com.barcodebite.shared.domain.model.ScanResult
import com.barcodebite.shared.domain.repository.ScanHistoryRepository

class ProductResultRepository(
    baseUrl: String,
    private val scanHistoryRepository: ScanHistoryRepository,
) {
    private val httpClient = HttpClientFactory.create()
    private val apiService = BarcodeBiteApiService(
        client = httpClient,
        baseUrl = baseUrl,
    )

    suspend fun fetch(barcode: String): ProductResultData {
        val product = apiService.getProductByBarcode(barcode).toDomain()
        val score = apiService.analyze(barcode).toDomain()
        runCatching {
            scanHistoryRepository.save(
                ScanResult(
                    product = product,
                    nutritionScore = score,
                ),
            )
        }

        return ProductResultData(product = product, score = score)
    }

    fun close() {
        httpClient.close()
    }
}

data class ProductResultData(
    val product: Product,
    val score: NutritionScore,
)
