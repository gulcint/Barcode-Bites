package com.barcodebite.backend.external

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class OpenFoodFactsClientTest {

    @Test
    fun mapPayloadBuildsProductRecordWithNutriments() {
        val payload =
            """
            {
              "status": 1,
              "product": {
                "product_name": "Organic Oat Bar",
                "brands": "Nature Co, Secondary",
                "ingredients_text": "Whole oats, Honey, Sea Salt",
                "additives_tags": ["en:e330", "en:e500"],
                "nutriments": {
                  "energy-kcal_100g": 380.0,
                  "proteins_100g": 8.5,
                  "carbohydrates_100g": 62.0,
                  "fat_100g": 9.2,
                  "sugars_100g": 18.1,
                  "salt_100g": 0.45
                }
              }
            }
            """.trimIndent()

        val product = mapOpenFoodFactsPayload("8690504012345", payload)

        assertNotNull(product)
        assertEquals("8690504012345", product.barcode)
        assertEquals("Organic Oat Bar", product.name)
        assertEquals("Nature Co", product.brand)
        assertEquals("Whole oats, Honey, Sea Salt", product.ingredients)
        assertEquals(listOf("e330", "e500"), product.additives)
        assertEquals(380.0, product.nutrition.calories)
        assertEquals(8.5, product.nutrition.protein)
        assertEquals(62.0, product.nutrition.carbohydrates)
        assertEquals(9.2, product.nutrition.fat)
        assertEquals(18.1, product.nutrition.sugar)
        assertEquals(0.45, product.nutrition.salt)
    }

    @Test
    fun mapPayloadFallsBackToDefaultsWhenFieldsMissing() {
        val payload =
            """
            {
              "status": 1,
              "product": {
                "brands": "",
                "nutriments": {}
              }
            }
            """.trimIndent()

        val product = mapOpenFoodFactsPayload("123", payload)

        assertNotNull(product)
        assertEquals("Unknown Product", product.name)
        assertEquals("Unknown Brand", product.brand)
        assertEquals("", product.ingredients)
        assertEquals(emptyList(), product.additives)
        assertEquals(0.0, product.nutrition.calories)
        assertEquals(0.0, product.nutrition.salt)
    }

    @Test
    fun mapPayloadReturnsNullForInvalidJson() {
        val product = mapOpenFoodFactsPayload("123", "{not-json}")

        assertNull(product)
    }
}
