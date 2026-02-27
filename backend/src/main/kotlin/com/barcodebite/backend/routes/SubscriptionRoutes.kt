package com.barcodebite.backend.routes

import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionPlaceholderResponse(
    val plan: String,
    val active: Boolean,
)

fun Route.subscriptionRoutes() {
    route("/subscriptions") {
        get("/status") {
            call.respond(SubscriptionPlaceholderResponse(plan = "free", active = true))
        }
    }
}
