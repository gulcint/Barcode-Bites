package com.barcodebite.android.data.compare

import com.barcodebite.shared.data.remote.HttpClientFactory
import com.barcodebite.shared.data.remote.api.BarcodeBiteApiService

class CompareRepository(
    baseUrl: String,
) {
    private val httpClient = HttpClientFactory.create()
    private val apiService = BarcodeBiteApiService(
        client = httpClient,
        baseUrl = baseUrl,
    )

    suspend fun compare(firstBarcode: String, secondBarcode: String): CompareResultData {
        val result = apiService.compare(firstBarcode, secondBarcode)
        return CompareResultData(
            firstBarcode = result.first.barcode,
            firstGrade = result.first.grade,
            firstScore = result.first.score,
            secondBarcode = result.second.barcode,
            secondGrade = result.second.grade,
            secondScore = result.second.score,
            recommendation = result.recommendation,
        )
    }

    fun close() {
        httpClient.close()
    }
}

data class CompareResultData(
    val firstBarcode: String,
    val firstGrade: String,
    val firstScore: Int,
    val secondBarcode: String,
    val secondGrade: String,
    val secondScore: Int,
    val recommendation: String,
)
