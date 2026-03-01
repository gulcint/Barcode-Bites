package com.barcodebite.backend.routes

import com.barcodebite.backend.model.ErrorResponse
import com.barcodebite.backend.service.AnalysisService
import com.barcodebite.backend.service.RateLimitService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.plugins.origin
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
    val cleanLabelScore: Int,
    val cleanLabelVerdict: String,
    val isJunkFood: Boolean,
    val junkFoodReasons: List<String>,
    val summary: String,
)

@Serializable
data class AnalysisCompareRequest(
    val firstBarcode: String,
    val secondBarcode: String,
)

@Serializable
data class AnalysisCompareResponse(
    val first: AnalysisResponse,
    val second: AnalysisResponse,
    val recommendation: String,
)

fun Route.analysisRoutes(
    analysisService: AnalysisService,
    rateLimitService: RateLimitService,
) {
    route("/analysis") {
        post {
            val request = call.receive<AnalysisRequest>()
            if (request.barcode.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("barcode is required"))
                return@post
            }
            val sourceKey = call.request.origin.remoteHost.ifBlank { "unknown" }
            if (!rateLimitService.consume(sourceKey)) {
                call.respond(HttpStatusCode.TooManyRequests, ErrorResponse("daily free scan limit reached"))
                return@post
            }

            val result = analysisService.analyze(request.barcode)
            call.respond(
                AnalysisResponse(
                    barcode = result.barcode,
                    score = result.score,
                    grade = result.grade,
                    cleanLabelScore = result.cleanLabelScore,
                    cleanLabelVerdict = result.cleanLabelVerdict,
                    isJunkFood = result.isJunkFood,
                    junkFoodReasons = result.junkFoodReasons,
                    summary = result.summary,
                ),
            )
        }

        post("/compare") {
            val request = call.receive<AnalysisCompareRequest>()
            if (request.firstBarcode.isBlank() || request.secondBarcode.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("both barcodes are required"))
                return@post
            }
            val result = analysisService.compare(request.firstBarcode, request.secondBarcode)
            call.respond(
                AnalysisCompareResponse(
                    first = AnalysisResponse(
                        barcode = result.first.barcode,
                        score = result.first.score,
                        grade = result.first.grade,
                        cleanLabelScore = result.first.cleanLabelScore,
                        cleanLabelVerdict = result.first.cleanLabelVerdict,
                        isJunkFood = result.first.isJunkFood,
                        junkFoodReasons = result.first.junkFoodReasons,
                        summary = result.first.summary,
                    ),
                    second = AnalysisResponse(
                        barcode = result.second.barcode,
                        score = result.second.score,
                        grade = result.second.grade,
                        cleanLabelScore = result.second.cleanLabelScore,
                        cleanLabelVerdict = result.second.cleanLabelVerdict,
                        isJunkFood = result.second.isJunkFood,
                        junkFoodReasons = result.second.junkFoodReasons,
                        summary = result.second.summary,
                    ),
                    recommendation = result.recommendation,
                ),
            )
        }
    }
}
