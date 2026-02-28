package com.barcodebite.android.data.history

import android.content.Context
import com.barcodebite.shared.data.local.DatabaseDriverFactory
import com.barcodebite.shared.data.local.history.ScanHistoryLocalDataSource
import com.barcodebite.shared.data.local.history.SqlDelightScanHistoryRepository
import com.barcodebite.shared.domain.repository.ScanHistoryRepository
import com.barcodebite.shared.local.BarcodeBiteDatabase

class HistoryContainer(
    context: Context,
) {
    private val databaseDriver = DatabaseDriverFactory(context).createDriver()
    private val database = BarcodeBiteDatabase(databaseDriver)
    private val localDataSource = ScanHistoryLocalDataSource(database)

    val scanHistoryRepository: ScanHistoryRepository = SqlDelightScanHistoryRepository(localDataSource)
}
