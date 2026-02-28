package com.barcodebite.shared.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.barcodebite.shared.local.BarcodeBiteDatabase

class DatabaseDriverFactory(
    private val context: Context,
) {
    fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = BarcodeBiteDatabase.Schema,
            context = context,
            name = "barcodebite.db",
        )
    }
}
