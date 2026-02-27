package com.barcodebite.shared

import com.barcodebite.shared.data.remote.api.BarcodeBiteApiService
import com.barcodebite.shared.data.remote.dto.NutritionDto
import com.barcodebite.shared.data.remote.dto.ProductDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json

class RemoteContractTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun productDtoRoundTripSerialization() {
        val dto = ProductDto(
            barcode = "8690504012345",
            name = "Test Product",
            brand = "BarcodeBite",
            nutrition = NutritionDto(
                calories = 240.0,
                protein = 9.5,
                carbohydrates = 32.0,
                fat = 6.5,
                sugar = 11.0,
                salt = 0.8,
            ),
        )

        val encoded = json.encodeToString(ProductDto.serializer(), dto)
        val decoded = json.decodeFromString(ProductDto.serializer(), encoded)

        assertEquals(dto, decoded)
    }

    @Test
    fun productPathUsesVersionedPrefix() {
        val path = BarcodeBiteApiService.productByBarcodePath("8690504012345")

        assertEquals("/v1/products/8690504012345", path)
    }
}
