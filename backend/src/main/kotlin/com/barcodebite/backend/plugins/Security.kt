package com.barcodebite.backend.plugins

import com.barcodebite.backend.config.JwtConfig
import com.barcodebite.backend.security.CurrentUserPrincipal
import com.barcodebite.backend.service.JwtService
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt

fun Application.configureSecurity(jwtConfig: JwtConfig) {
    val jwtService = JwtService(jwtConfig)

    install(Authentication) {
        jwt("auth-jwt") {
            verifier(jwtService.verifier())
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asInt()
                val email = credential.payload.getClaim("email").asString()
                if (userId == null || email.isNullOrBlank()) {
                    null
                } else {
                    CurrentUserPrincipal(id = userId, email = email)
                }
            }
        }
    }
}
