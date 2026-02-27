package com.barcodebite.backend.service

import com.barcodebite.backend.external.ExternalProductLookup
import com.barcodebite.backend.model.NutritionValues
import com.barcodebite.backend.model.ProductRecord
import com.barcodebite.backend.repository.ProductRepository

class ProductService(
    private val repository: ProductRepository,
    private val externalProductLookup: ExternalProductLookup,
) {
    fun getOrCreate(barcode: String): ProductRecord {
        repository.findByBarcode(barcode)?.let { return it }

        val fromOpenFoodFacts = externalProductLookup.fetchProduct(barcode)
        return repository.upsert(fromOpenFoodFacts ?: unknownProduct(barcode))
    }

    fun createOrUpdate(product: ProductRecord): ProductRecord {
        return repository.upsert(product)
    }

    private fun unknownProduct(barcode: String): ProductRecord {
        return ProductRecord(
            barcode = barcode,
            name = "Unknown Product",
            brand = "Unknown Brand",
            nutrition = NutritionValues.empty(),
            ingredients = "",
            additives = emptyList(),
        )
    }
}
