package com.barcodebite.backend.security

import io.ktor.server.auth.Principal

data class CurrentUserPrincipal(
    val id: Int,
    val email: String,
) : Principal
