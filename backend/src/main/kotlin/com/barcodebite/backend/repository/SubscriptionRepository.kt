package com.barcodebite.backend.repository

import com.barcodebite.backend.model.SubscriptionRecord
import com.barcodebite.backend.persistence.table.SubscriptionsTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class SubscriptionRepository {
    fun findByUserId(userId: Int): SubscriptionRecord? {
        return transaction {
            SubscriptionsTable
                .selectAll()
                .where { SubscriptionsTable.userId eq userId }
                .limit(1)
                .map(::rowToSubscription)
                .firstOrNull()
        }
    }

    fun upsert(record: SubscriptionRecord): SubscriptionRecord {
        return transaction {
            val updatedRows = SubscriptionsTable.update(where = { SubscriptionsTable.userId eq record.userId }) {
                it[plan] = record.plan
                it[isActive] = record.isActive
                it[expiresAtEpochMs] = record.expiresAtEpochMs
            }
            if (updatedRows == 0) {
                SubscriptionsTable.insert {
                    it[userId] = record.userId
                    it[plan] = record.plan
                    it[isActive] = record.isActive
                    it[expiresAtEpochMs] = record.expiresAtEpochMs
                }
            }
            record
        }
    }

    private fun rowToSubscription(row: ResultRow): SubscriptionRecord {
        return SubscriptionRecord(
            userId = row[SubscriptionsTable.userId].value,
            plan = row[SubscriptionsTable.plan],
            isActive = row[SubscriptionsTable.isActive],
            expiresAtEpochMs = row[SubscriptionsTable.expiresAtEpochMs],
        )
    }
}
