package com.barcodebite.backend.routes

import com.barcodebite.backend.model.ErrorResponse
import com.barcodebite.backend.model.NutritionValues
import com.barcodebite.backend.model.ProductRecord
import com.barcodebite.backend.service.ProductService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

@Serializable
data class NutritionPayload(
    val calories: Double,
    val protein: Double,
    val carbohydrates: Double,
    val fat: Double,
    val sugar: Double,
    val salt: Double,
)

@Serializable
data class ProductRequest(
    val barcode: String,
    val name: String,
    val brand: String,
    val nutrition: NutritionPayload,
)

@Serializable
data class ProductResponse(
    val barcode: String,
    val name: String,
    val brand: String,
    val nutrition: NutritionPayload,
)

fun Route.productRoutes(productService: ProductService) {
    route("/products") {
        get("/{barcode}") {
            val barcode = call.parameters["barcode"]
            if (barcode.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("barcode is required"))
                return@get
            }

            val product = productService.getOrCreate(barcode)
            call.respond(product.toResponse())
        }

        post {
            val request = call.receive<ProductRequest>()
            if (request.barcode.isBlank() || request.name.isBlank() || request.brand.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("barcode, name and brand are required"))
                return@post
            }

            val saved = productService.createOrUpdate(request.toDomain())
            call.respond(HttpStatusCode.Created, saved.toResponse())
        }
    }
}

private fun ProductRequest.toDomain(): ProductRecord {
    return ProductRecord(
        barcode = barcode,
        name = name,
        brand = brand,
        nutrition = NutritionValues(
            calories = nutrition.calories,
            protein = nutrition.protein,
            carbohydrates = nutrition.carbohydrates,
            fat = nutrition.fat,
            sugar = nutrition.sugar,
            salt = nutrition.salt,
        ),
    )
}

private fun ProductRecord.toResponse(): ProductResponse {
    return ProductResponse(
        barcode = barcode,
        name = name,
        brand = brand,
        nutrition = NutritionPayload(
            calories = nutrition.calories,
            protein = nutrition.protein,
            carbohydrates = nutrition.carbohydrates,
            fat = nutrition.fat,
            sugar = nutrition.sugar,
            salt = nutrition.salt,
        ),
    )
}
