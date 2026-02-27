package com.barcodebite.shared.domain.model

data class Product(
    val barcode: String,
    val name: String,
    val brand: String,
    val nutrition: Nutrition,
    val additives: List<Additive> = emptyList(),
)
