package com.barcodebite.shared

import com.barcodebite.shared.data.remote.api.BarcodeBiteApiService
import com.barcodebite.shared.data.remote.dto.NutritionDto
import com.barcodebite.shared.data.remote.dto.ProductDto
import com.barcodebite.shared.data.remote.dto.SubscriptionStatusDto
import com.barcodebite.shared.data.remote.dto.UserProfileDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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

    @Test
    fun profileAndSubscriptionDtoDeserializationUsesDefaults() {
        val profileJson = """{"id":7,"email":"user@barcodebite.dev","displayName":"User","createdAtEpochMs":1700000000000}"""
        val profile = json.decodeFromString(UserProfileDto.serializer(), profileJson)
        assertEquals(7, profile.id)
        assertEquals("user@barcodebite.dev", profile.email)
        assertEquals("User", profile.displayName)
        assertEquals(1700000000000, profile.createdAtEpochMs)

        val subscriptionJson = """{"plan":"free","isActive":false}"""
        val subscription = json.decodeFromString(SubscriptionStatusDto.serializer(), subscriptionJson)
        assertEquals("free", subscription.plan)
        assertFalse(subscription.isActive)
        assertEquals(null, subscription.expiresAtEpochMs)
        assertFalse(subscription.entitlements.compare)
        assertFalse(subscription.entitlements.advancedAnalysis)
        assertFalse(subscription.entitlements.unlimitedScans)
    }
}
