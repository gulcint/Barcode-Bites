package com.barcodebite.backend.service

import com.barcodebite.backend.external.ExternalProductLookup
import com.barcodebite.backend.model.NutritionValues
import com.barcodebite.backend.model.ProductRecord
import com.barcodebite.backend.repository.ProductRepository

class ProductService(
    private val repository: ProductRepository,
    private val externalProductLookup: ExternalProductLookup,
    private val additiveCatalogService: AdditiveCatalogService,
) {
    fun getOrCreate(barcode: String): ProductRecord {
        repository.findByBarcode(barcode)?.let { return it }

        val fromOpenFoodFacts = externalProductLookup.fetchProduct(barcode)
        return repository.upsert(fromOpenFoodFacts ?: unknownProduct(barcode))
    }

    fun createOrUpdate(product: ProductRecord): ProductRecord {
        return repository.upsert(product)
    }

    fun resolveAdditives(codes: List<String>): List<AdditiveCatalogEntry> {
        return additiveCatalogService.resolve(codes)
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
