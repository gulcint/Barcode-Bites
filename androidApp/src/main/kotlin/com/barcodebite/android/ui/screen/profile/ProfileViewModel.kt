package com.barcodebite.android.ui.screen.profile

import com.barcodebite.android.data.profile.ProfileData
import com.barcodebite.android.data.profile.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val data: ProfileData) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val accessTokenProvider: () -> String?,
) {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    suspend fun load() {
        _uiState.update { ProfileUiState.Loading }
        runCatching {
            profileRepository.fetch(accessTokenProvider())
        }.onSuccess { data ->
            _uiState.update { ProfileUiState.Success(data) }
        }.onFailure {
            _uiState.update {
                ProfileUiState.Error("Profil bilgisi yuklenemedi.")
            }
        }
    }
}
