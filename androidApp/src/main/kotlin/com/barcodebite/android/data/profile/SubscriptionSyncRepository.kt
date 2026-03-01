package com.barcodebite.android.data.profile

import com.barcodebite.shared.data.remote.HttpClientFactory
import com.barcodebite.shared.data.remote.api.BarcodeBiteApiService
import com.barcodebite.shared.data.remote.dto.toDomain
import com.barcodebite.shared.domain.model.SubscriptionStatus

class SubscriptionSyncRepository(
    baseUrl: String,
) {
    private val httpClient = HttpClientFactory.create()
    private val apiService = BarcodeBiteApiService(
        client = httpClient,
        baseUrl = baseUrl,
    )

    suspend fun verify(plan: String, purchaseToken: String, accessToken: String?): SubscriptionStatus {
        return apiService.verifySubscription(
            plan = plan,
            purchaseToken = purchaseToken,
            accessToken = accessToken,
        ).toDomain()
    }

    suspend fun restore(purchaseToken: String, accessToken: String?): SubscriptionStatus {
        return apiService.restoreSubscription(
            purchaseToken = purchaseToken,
            accessToken = accessToken,
        ).toDomain()
    }

    fun close() {
        httpClient.close()
    }
}
