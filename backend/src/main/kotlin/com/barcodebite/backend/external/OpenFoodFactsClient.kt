package com.barcodebite.backend.external

import com.barcodebite.backend.config.OpenFoodFactsConfig
import com.barcodebite.backend.model.NutritionValues
import com.barcodebite.backend.model.ProductRecord
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

interface ExternalProductLookup {
    fun fetchProduct(barcode: String): ProductRecord?
}

class OpenFoodFactsClient(
    private val config: OpenFoodFactsConfig,
    private val httpClient: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofMillis(config.timeoutMillis))
        .build(),
    private val json: Json = Json { ignoreUnknownKeys = true },
) : ExternalProductLookup {
    override fun fetchProduct(barcode: String): ProductRecord? {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("${config.baseUrl}/product/$barcode.json"))
            .header("Accept", "application/json")
            .header("User-Agent", config.userAgent)
            .timeout(Duration.ofMillis(config.timeoutMillis))
            .GET()
            .build()

        return runCatching {
            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() !in 200..299) {
                return null
            }
            mapOpenFoodFactsPayload(barcode, response.body(), json)
        }.getOrNull()
    }
}

fun mapOpenFoodFactsPayload(barcode: String, payload: String, json: Json = Json { ignoreUnknownKeys = true }): ProductRecord? {
    val parsed = runCatching { json.decodeFromString<OpenFoodFactsResponse>(payload) }.getOrNull() ?: return null
    val product = parsed.product ?: return null

    val name = product.productName.orUnknown(defaultValue = "Unknown Product")
    val brand = product.brands
        ?.split(',')
        ?.firstOrNull()
        ?.trim()
        .orUnknown(defaultValue = "Unknown Brand")

    val nutriments = product.nutriments
    return ProductRecord(
        barcode = barcode,
        name = name,
        brand = brand,
        nutrition = NutritionValues(
            calories = nutriments?.energyKcal100g.asSafeNutrition(),
            protein = nutriments?.proteins100g.asSafeNutrition(),
            carbohydrates = nutriments?.carbohydrates100g.asSafeNutrition(),
            fat = nutriments?.fat100g.asSafeNutrition(),
            sugar = nutriments?.sugars100g.asSafeNutrition(),
            salt = nutriments?.salt100g.asSafeNutrition(),
        ),
    )
}

private fun String?.orUnknown(defaultValue: String): String {
    return this?.trim().takeUnless { it.isNullOrBlank() } ?: defaultValue
}

private fun Double?.asSafeNutrition(): Double {
    return (this ?: 0.0).coerceAtLeast(0.0)
}

@Serializable
private data class OpenFoodFactsResponse(
    val status: Int? = null,
    val product: OpenFoodFactsProduct? = null,
)

@Serializable
private data class OpenFoodFactsProduct(
    @SerialName("product_name")
    val productName: String? = null,
    val brands: String? = null,
    val nutriments: OpenFoodFactsNutriments? = null,
)

@Serializable
private data class OpenFoodFactsNutriments(
    @SerialName("energy-kcal_100g")
    val energyKcal100g: Double? = null,
    @SerialName("proteins_100g")
    val proteins100g: Double? = null,
    @SerialName("carbohydrates_100g")
    val carbohydrates100g: Double? = null,
    @SerialName("fat_100g")
    val fat100g: Double? = null,
    @SerialName("sugars_100g")
    val sugars100g: Double? = null,
    @SerialName("salt_100g")
    val salt100g: Double? = null,
)
