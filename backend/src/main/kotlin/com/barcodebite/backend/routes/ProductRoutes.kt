package com.barcodebite.backend.routes

import com.barcodebite.backend.model.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val barcode: String,
    val name: String,
    val brand: String,
)

fun Route.productRoutes() {
    route("/products") {
        get("/{barcode}") {
            val barcode = call.parameters["barcode"]
            if (barcode.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("barcode is required"))
                return@get
            }

            call.respond(
                ProductResponse(
                    barcode = barcode,
                    name = "Unknown Product",
                    brand = "Unknown Brand",
                ),
            )
        }
    }
}
