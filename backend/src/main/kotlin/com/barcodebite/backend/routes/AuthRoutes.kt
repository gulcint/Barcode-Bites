package com.barcodebite.backend.routes

import com.barcodebite.backend.model.ErrorResponse
import com.barcodebite.backend.service.AuthResult
import com.barcodebite.backend.service.AuthService
import com.barcodebite.backend.service.LoginResult
import com.barcodebite.backend.service.RegisterResult
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class AuthResponse(
    val userId: Int,
    val email: String,
    val accessToken: String,
)

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()

            when (val result = authService.register(request.email, request.password)) {
                is RegisterResult.Success -> {
                    call.respond(HttpStatusCode.Created, result.auth.toResponse())
                }
                RegisterResult.InvalidInput -> {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("email or password is invalid"))
                }
                RegisterResult.DuplicateEmail -> {
                    call.respond(HttpStatusCode.Conflict, ErrorResponse("email is already registered"))
                }
            }
        }

        post("/login") {
            val request = call.receive<LoginRequest>()

            when (val result = authService.login(request.email, request.password)) {
                is LoginResult.Success -> {
                    call.respond(result.auth.toResponse())
                }
                LoginResult.InvalidCredentials -> {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse("invalid credentials"))
                }
            }
        }
    }
}

private fun AuthResult.toResponse(): AuthResponse {
    return AuthResponse(
        userId = userId,
        email = email,
        accessToken = accessToken,
    )
}
