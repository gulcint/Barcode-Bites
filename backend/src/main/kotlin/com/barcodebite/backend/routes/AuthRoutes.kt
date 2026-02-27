package com.barcodebite.backend.routes

import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

@Serializable
data class AuthPingResponse(
    val service: String,
    val ready: Boolean,
)

fun Route.authRoutes() {
    route("/auth") {
        get("/ping") {
            call.respond(AuthPingResponse(service = "auth", ready = false))
        }
    }
}
