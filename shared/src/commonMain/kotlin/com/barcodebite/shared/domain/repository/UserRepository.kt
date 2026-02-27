package com.barcodebite.shared.domain.repository

interface UserRepository {
    suspend fun isPremiumUser(userId: String): Boolean

    suspend fun remainingFreeScans(userId: String): Int
}
