package com.barcodebite.shared.domain.repository

import com.barcodebite.shared.domain.model.ScanResult

interface ScanHistoryRepository {
    suspend fun getRecent(limit: Int = 20): List<ScanResult>

    suspend fun save(result: ScanResult)
}
