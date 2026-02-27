package com.barcodebite.backend.service

import com.barcodebite.backend.model.NutritionValues
import com.barcodebite.backend.model.ProductRecord
import com.barcodebite.backend.repository.ProductRepository

class ProductService(private val repository: ProductRepository) {
    fun getOrCreate(barcode: String): ProductRecord {
        return repository.findByBarcode(barcode) ?: repository.upsert(
            ProductRecord(
                barcode = barcode,
                name = "Unknown Product",
                brand = "Unknown Brand",
                nutrition = NutritionValues.empty(),
            ),
        )
    }

    fun createOrUpdate(product: ProductRecord): ProductRecord {
        return repository.upsert(product)
    }
}
