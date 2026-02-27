package com.barcodebite.backend.routes

import com.barcodebite.backend.model.ErrorResponse
import com.barcodebite.backend.service.AnalysisService
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

fun Route.analysisRoutes(analysisService: AnalysisService) {
    route("/analysis") {
        post {
            val request = call.receive<AnalysisRequest>()
            if (request.barcode.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("barcode is required"))
                return@post
            }

            val result = analysisService.analyze(request.barcode)
            call.respond(
                AnalysisResponse(
                    barcode = result.barcode,
                    score = result.score,
                    grade = result.grade,
                    summary = result.summary,
                ),
            )
        }
    }
}
