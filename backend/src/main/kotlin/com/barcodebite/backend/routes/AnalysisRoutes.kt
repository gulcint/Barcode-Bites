package com.barcodebite.backend.routes

import com.barcodebite.backend.model.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

@Serializable
data class AnalysisRequest(
    val barcode: String,
)

@Serializable
data class AnalysisResponse(
    val barcode: String,
    val score: Int,
    val grade: String,
    val summary: String,
)

fun Route.analysisRoutes() {
    route("/analysis") {
        post {
            val request = call.receive<AnalysisRequest>()
            if (request.barcode.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("barcode is required"))
                return@post
            }

            val score = 68
            val grade = when {
                score >= 85 -> "A"
                score >= 70 -> "B"
                score >= 55 -> "C"
                score >= 40 -> "D"
                else -> "E"
            }

            call.respond(
                AnalysisResponse(
                    barcode = request.barcode,
                    score = score,
                    grade = grade,
                    summary = "Preliminary analysis for phase 1.",
                ),
            )
        }
    }
}
