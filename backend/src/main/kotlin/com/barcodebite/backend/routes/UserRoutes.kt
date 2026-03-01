package com.barcodebite.backend.routes

import com.barcodebite.backend.repository.UserRepository
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
    val displayName: String,
    val createdAtEpochMs: Long,
)

fun Route.userRoutes(
    userRepository: UserRepository,
) {
    route("/users") {
        authenticate("auth-jwt") {
            get("/me") {
                val principal = call.principal<CurrentUserPrincipal>()
                if (principal == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@get
                }

                val user = userRepository.findById(principal.id)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }

                call.respond(
                    UserMeResponse(
                        id = user.id,
                        email = user.email,
                        displayName = user.displayName,
                        createdAtEpochMs = user.createdAtEpochMs,
                    ),
                )
            }
        }
    }
}
