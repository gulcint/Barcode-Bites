package com.barcodebite.shared.data.remote.api

import com.barcodebite.shared.data.remote.dto.AnalysisDto
import com.barcodebite.shared.data.remote.dto.ProductDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.Serializable

class BarcodeBiteApiService(
    private val client: HttpClient,
    private val baseUrl: String,
) {
    suspend fun getProductByBarcode(barcode: String): ProductDto {
        return client.get(fullPath(productByBarcodePath(barcode))).body()
    }

    suspend fun analyze(barcode: String): AnalysisDto {
        return client.post(fullPath(analysisPath)) {
            setBody(AnalysisRequestDto(barcode = barcode))
        }.body()
    }

    private fun fullPath(path: String): String = "$baseUrl$path"

    companion object {
        fun productByBarcodePath(barcode: String): String = "/v1/products/$barcode"
        const val analysisPath: String = "/v1/analysis"
    }
}

@Serializable
private data class AnalysisRequestDto(
    val barcode: String,
)
