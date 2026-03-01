package com.barcodebite.android.ui.screen.compare

import com.barcodebite.android.data.compare.CompareRepository
import com.barcodebite.android.data.compare.CompareResultData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed interface CompareUiState {
    data object Idle : CompareUiState
    data object Loading : CompareUiState
    data class Success(val data: CompareResultData) : CompareUiState
    data class Error(val message: String) : CompareUiState
}

class CompareViewModel(
    private val repository: CompareRepository,
) {
    private val _uiState = MutableStateFlow<CompareUiState>(CompareUiState.Idle)
    val uiState: StateFlow<CompareUiState> = _uiState.asStateFlow()

    suspend fun compare(firstBarcode: String, secondBarcode: String) {
        if (firstBarcode.isBlank() || secondBarcode.isBlank()) {
            _uiState.update { CompareUiState.Error("Iki barkod da gerekli") }
            return
        }
        _uiState.update { CompareUiState.Loading }
        runCatching {
            repository.compare(firstBarcode.trim(), secondBarcode.trim())
        }.onSuccess { data ->
            _uiState.update { CompareUiState.Success(data) }
        }.onFailure {
            _uiState.update { CompareUiState.Error("Karsilastirma alinamadi") }
        }
    }
}
