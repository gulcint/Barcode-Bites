package com.barcodebite.backend.service

import com.barcodebite.backend.model.UserRecord
import com.barcodebite.backend.repository.UserRepository
import org.mindrot.jbcrypt.BCrypt

sealed interface RegisterResult {
    data class Success(val auth: AuthResult) : RegisterResult
    data object InvalidInput : RegisterResult
    data object DuplicateEmail : RegisterResult
}

sealed interface LoginResult {
    data class Success(val auth: AuthResult) : LoginResult
    data object InvalidCredentials : LoginResult
}

data class AuthResult(
    val userId: Int,
    val email: String,
    val accessToken: String,
)

class AuthService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
) {
    fun register(email: String, password: String): RegisterResult {
        val normalizedEmail = email.trim().lowercase()
        if (!isValidEmail(normalizedEmail) || password.length < 8) {
            return RegisterResult.InvalidInput
        }

        if (userRepository.findByEmail(normalizedEmail) != null) {
            return RegisterResult.DuplicateEmail
        }

        val createdUser = userRepository.createUser(
            email = normalizedEmail,
            passwordHash = BCrypt.hashpw(password, BCrypt.gensalt()),
        ) ?: return RegisterResult.DuplicateEmail

        return RegisterResult.Success(toAuthResult(createdUser))
    }

    fun login(email: String, password: String): LoginResult {
        val user = userRepository.findByEmail(email.trim().lowercase()) ?: return LoginResult.InvalidCredentials
        if (!BCrypt.checkpw(password, user.passwordHash)) {
            return LoginResult.InvalidCredentials
        }

        return LoginResult.Success(toAuthResult(user))
    }

    private fun toAuthResult(user: UserRecord): AuthResult {
        return AuthResult(
            userId = user.id,
            email = user.email,
            accessToken = jwtService.generateAccessToken(user),
        )
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.length >= 5
    }
}
