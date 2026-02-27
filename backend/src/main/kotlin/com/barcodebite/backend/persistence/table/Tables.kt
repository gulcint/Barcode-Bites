package com.barcodebite.backend.persistence.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

object UsersTable : IntIdTable("users") {
    val email = varchar("email", length = 255).uniqueIndex()
    val passwordHash = varchar("password_hash", length = 72)
    val createdAtEpochMs = long("created_at_epoch_ms")
}

object ProductsTable : Table("products") {
    val barcode = varchar("barcode", length = 32)
    val name = varchar("name", length = 255)
    val brand = varchar("brand", length = 255)
    val calories = double("calories")
    val protein = double("protein")
    val carbohydrates = double("carbohydrates")
    val fat = double("fat")
    val sugar = double("sugar")
    val salt = double("salt")
    val ingredients = text("ingredients").default("")
    val additivesCsv = text("additives_csv").default("")

    override val primaryKey = PrimaryKey(barcode)
}

object ScanHistoryTable : IntIdTable("scan_history") {
    val userId = reference("user_id", UsersTable).nullable()
    val barcode = varchar("barcode", length = 32)
    val score = integer("score")
    val grade = varchar("grade", length = 2)
    val scannedAtEpochMs = long("scanned_at_epoch_ms")
}

object SubscriptionsTable : IntIdTable("subscriptions") {
    val userId = reference("user_id", UsersTable).uniqueIndex()
    val plan = varchar("plan", length = 32)
    val isActive = bool("is_active")
    val expiresAtEpochMs = long("expires_at_epoch_ms").nullable()
}
