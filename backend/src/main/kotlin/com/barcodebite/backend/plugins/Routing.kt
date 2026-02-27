package com.barcodebite.backend.plugins

import com.barcodebite.backend.model.HealthResponse
import com.barcodebite.backend.routes.analysisRoutes
import com.barcodebite.backend.routes.authRoutes
import com.barcodebite.backend.routes.productRoutes
import com.barcodebite.backend.routes.subscriptionRoutes
import com.barcodebite.backend.routes.userRoutes
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        get("/health") {
            call.respond(HealthResponse(status = "ok"))
        }

        route("/v1") {
            authRoutes()
            productRoutes()
            analysisRoutes()
            userRoutes()
            subscriptionRoutes()
        }
    }
}
