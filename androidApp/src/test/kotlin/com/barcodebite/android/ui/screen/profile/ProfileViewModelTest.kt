package com.barcodebite.android.ui.screen.profile

import com.barcodebite.android.data.profile.ProfileData
import com.barcodebite.android.data.profile.ProfileRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileViewModelTest {
    @Test
    fun load_withoutToken_usesGuestFallback() = runTest {
        val repository = object : ProfileRepository {
            override suspend fun fetch(accessToken: String?): ProfileData {
                return ProfileData.guest()
            }
        }
        val viewModel = ProfileViewModel(
            profileRepository = repository,
            accessTokenProvider = { null },
        )

        viewModel.load()

        val state = viewModel.uiState.value as ProfileUiState.Success
        assertEquals("Misafir Kullanici", state.data.profile.displayName)
        assertEquals("free", state.data.subscription.plan)
    }
}
