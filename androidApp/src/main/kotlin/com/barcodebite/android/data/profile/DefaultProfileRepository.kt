package com.barcodebite.android.data.profile

import com.barcodebite.shared.data.remote.HttpClientFactory
import com.barcodebite.shared.data.remote.api.BarcodeBiteApiService
import com.barcodebite.shared.data.remote.dto.toDomain

class DefaultProfileRepository(
    baseUrl: String,
) : ProfileRepository {
    private val httpClient = HttpClientFactory.create()
    private val apiService = BarcodeBiteApiService(
        client = httpClient,
        baseUrl = baseUrl,
    )

    override suspend fun fetch(accessToken: String?): ProfileData {
        if (accessToken.isNullOrBlank()) {
            return ProfileData.guest()
        }

        val profile = apiService.getUserProfile(accessToken).toDomain()
        val subscription = apiService.getSubscriptionStatus(accessToken).toDomain()
        return ProfileData(profile = profile, subscription = subscription)
    }

    fun close() {
        httpClient.close()
    }
}
