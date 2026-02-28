package com.barcodebite.shared.data.local.history

import com.barcodebite.shared.local.BarcodeBiteDatabase
import com.barcodebite.shared.local.Scan_history

class ScanHistoryLocalDataSource(
    private val database: BarcodeBiteDatabase,
) {
    private val queries = database.barcodeBiteDatabaseQueries

    fun insertOrReplace(
        id: String,
        barcode: String,
        name: String,
        brand: String,
        grade: String,
        score: Long,
        cleanLabelScore: Long,
        isJunkFood: Long,
        scannedAtEpochMs: Long,
    ) {
        queries.insertOrReplace(
            id = id,
            barcode = barcode,
            name = name,
            brand = brand,
            grade = grade,
            score = score,
            clean_label_score = cleanLabelScore,
            is_junk_food = isJunkFood,
            scanned_at_epoch_ms = scannedAtEpochMs,
        )
    }

    fun selectRecent(limit: Long): List<Scan_history> = queries.selectRecent(limit).executeAsList()

    fun countAll(): Long = queries.countAll().executeAsOne()

    fun deleteOverflow(keepCount: Long) {
        queries.deleteOverflow(keepCount)
    }

    fun deleteAll() {
        queries.deleteAll()
    }
}
