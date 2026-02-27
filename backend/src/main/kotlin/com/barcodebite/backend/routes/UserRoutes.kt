package com.barcodebite.backend.routes

import com.barcodebite.backend.security.CurrentUserPrincipal
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

@Serializable
data class UserMeResponse(
    val id: Int,
    val email: String,
)

fun Route.userRoutes() {
    route("/users") {
        authenticate("auth-jwt") {
            get("/me") {
                val principal = call.principal<CurrentUserPrincipal>()
                if (principal == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@get
                }

                call.respond(UserMeResponse(id = principal.id, email = principal.email))
            }
        }
    }
}
