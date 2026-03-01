package com.barcodebite.shared.data.remote.api

import com.barcodebite.shared.data.remote.dto.AnalysisDto
import com.barcodebite.shared.data.remote.dto.AnalysisCompareDto
import com.barcodebite.shared.data.remote.dto.ProductDto
import com.barcodebite.shared.data.remote.dto.SubscriptionStatusDto
import com.barcodebite.shared.data.remote.dto.UserProfileDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
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

    suspend fun compare(firstBarcode: String, secondBarcode: String): AnalysisCompareDto {
        return client.post(fullPath(analysisComparePath)) {
            setBody(
                AnalysisCompareRequestDto(
                    firstBarcode = firstBarcode,
                    secondBarcode = secondBarcode,
                ),
            )
        }.body()
    }

    suspend fun getUserProfile(accessToken: String?): UserProfileDto {
        return client.get(fullPath(userProfilePath)) {
            applyOptionalAuth(accessToken)
        }.body()
    }

    suspend fun getSubscriptionStatus(accessToken: String?): SubscriptionStatusDto {
        return client.get(fullPath(subscriptionStatusPath)) {
            applyOptionalAuth(accessToken)
        }.body()
    }

    private fun fullPath(path: String): String = "$baseUrl$path"

    private fun io.ktor.client.request.HttpRequestBuilder.applyOptionalAuth(accessToken: String?) {
        if (!accessToken.isNullOrBlank()) {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }
    }

    companion object {
        fun productByBarcodePath(barcode: String): String = "/v1/products/$barcode"
        const val analysisPath: String = "/v1/analysis"
        const val analysisComparePath: String = "/v1/analysis/compare"
        const val userProfilePath: String = "/v1/users/me"
        const val subscriptionStatusPath: String = "/v1/subscriptions/status"
    }
}

@Serializable
private data class AnalysisRequestDto(
    val barcode: String,
)

@Serializable
private data class AnalysisCompareRequestDto(
    val firstBarcode: String,
    val secondBarcode: String,
)
