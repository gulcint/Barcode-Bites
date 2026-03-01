package com.barcodebite.android.data.profile

import com.barcodebite.shared.domain.model.PremiumEntitlements
import com.barcodebite.shared.domain.model.SubscriptionStatus
import com.barcodebite.shared.domain.model.UserProfile

interface ProfileRepository {
    suspend fun fetch(accessToken: String?): ProfileData
}

data class ProfileData(
    val profile: UserProfile,
    val subscription: SubscriptionStatus,
) {
    companion object {
        fun guest(): ProfileData = ProfileData(
            profile = UserProfile(
                id = -1,
                email = "guest@barcodebite.local",
                displayName = "Misafir Kullanici",
                createdAtEpochMs = 0L,
            ),
            subscription = SubscriptionStatus(
                plan = "free",
                isActive = false,
                expiresAtEpochMs = null,
                entitlements = PremiumEntitlements(),
            ),
        )
    }
}
