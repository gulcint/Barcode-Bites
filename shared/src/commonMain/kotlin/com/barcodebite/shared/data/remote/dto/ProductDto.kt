package com.barcodebite.shared.data.remote.dto

import com.barcodebite.shared.domain.model.Nutrition
import com.barcodebite.shared.domain.model.Product
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val barcode: String,
    val name: String,
    val brand: String,
    val nutrition: NutritionDto,
)

fun ProductDto.toDomain(): Product = Product(
    barcode = barcode,
    name = name,
    brand = brand,
    nutrition = Nutrition(
        calories = nutrition.calories,
        protein = nutrition.protein,
        carbohydrates = nutrition.carbohydrates,
        fat = nutrition.fat,
        sugar = nutrition.sugar,
        salt = nutrition.salt,
    ),
)
