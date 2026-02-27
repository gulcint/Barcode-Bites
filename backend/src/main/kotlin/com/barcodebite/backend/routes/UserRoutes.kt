package com.barcodebite.backend.routes

import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

@Serializable
data class UserPlaceholderResponse(
    val message: String,
)

fun Route.userRoutes() {
    route("/users") {
        get("/me") {
            call.respond(UserPlaceholderResponse(message = "User module will be implemented in next step."))
        }
    }
}
