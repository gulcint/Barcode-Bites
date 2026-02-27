package com.barcodebite.backend.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.barcodebite.backend.config.JwtConfig
import com.barcodebite.backend.model.UserRecord
import java.time.Instant
import java.time.temporal.ChronoUnit

class JwtService(private val config: JwtConfig) {
    private val algorithm = Algorithm.HMAC256(config.secret)

    fun generateAccessToken(user: UserRecord): String {
        val now = Instant.now()
        val expiresAt = now.plus(config.expiresInHours, ChronoUnit.HOURS)

        return JWT.create()
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .withIssuedAt(now)
            .withExpiresAt(expiresAt)
            .withClaim("userId", user.id)
            .withClaim("email", user.email)
            .sign(algorithm)
    }

    fun verifier(): JWTVerifier {
        return JWT.require(algorithm)
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .build()
    }
}
