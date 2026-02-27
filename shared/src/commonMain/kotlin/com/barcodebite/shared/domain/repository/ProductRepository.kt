package com.barcodebite.shared.domain.repository

import com.barcodebite.shared.domain.model.Product
import com.barcodebite.shared.domain.model.ScanResult

interface ProductRepository {
    suspend fun getProductByBarcode(barcode: String): Product

    suspend fun analyzeProduct(barcode: String): ScanResult
}
