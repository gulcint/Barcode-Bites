package com.barcodebite.android.ui.screen.history

import com.barcodebite.shared.domain.model.ScanResult
import com.barcodebite.shared.domain.repository.ScanHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data object Empty : HistoryUiState
    data class Content(val items: List<ScanResult>) : HistoryUiState
    data class Error(val message: String) : HistoryUiState
}

class HistoryViewModel(
    private val scanHistoryRepository: ScanHistoryRepository,
) {
    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    suspend fun load() {
        runCatching { scanHistoryRepository.getRecent(limit = 20) }
            .onSuccess { items ->
                _uiState.update {
                    if (items.isEmpty()) {
                        HistoryUiState.Empty
                    } else {
                        HistoryUiState.Content(items)
                    }
                }
            }
            .onFailure {
                _uiState.update { HistoryUiState.Error("Tarama geçmişi yüklenemedi.") }
            }
    }

    suspend fun clear() {
        runCatching { scanHistoryRepository.clear() }
            .onSuccess { _uiState.update { HistoryUiState.Empty } }
            .onFailure { _uiState.update { HistoryUiState.Error("Tarama geçmişi temizlenemedi.") } }
    }
}
