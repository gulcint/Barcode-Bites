package com.barcodebite.backend.persistence

import com.barcodebite.backend.config.DatabaseConfig
import com.barcodebite.backend.persistence.table.ProductsTable
import com.barcodebite.backend.persistence.table.ScanHistoryTable
import com.barcodebite.backend.persistence.table.SubscriptionsTable
import com.barcodebite.backend.persistence.table.UsersTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseFactory(private val config: DatabaseConfig) {
    fun init() {
        val hikariDataSource = HikariDataSource(
            HikariConfig().apply {
                jdbcUrl = config.url
                username = config.user
                password = config.password
                driverClassName = config.driver
                maximumPoolSize = config.maxPoolSize
                isAutoCommit = false
                transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                validate()
            },
        )

        Database.connect(hikariDataSource)
        transaction {
            SchemaUtils.createMissingTablesAndColumns(UsersTable, ProductsTable, ScanHistoryTable, SubscriptionsTable)
        }
    }
}
