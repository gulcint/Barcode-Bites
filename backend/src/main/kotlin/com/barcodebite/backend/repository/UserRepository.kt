package com.barcodebite.backend.repository

import com.barcodebite.backend.model.UserRecord
import com.barcodebite.backend.persistence.table.UsersTable
import java.time.Instant
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {
    fun createUser(email: String, passwordHash: String): UserRecord? {
        return try {
            transaction {
                val insert = UsersTable.insert {
                    it[UsersTable.email] = email
                    it[UsersTable.passwordHash] = passwordHash
                    it[displayName] = email.substringBefore("@").ifBlank { "BarcodeBite User" }
                    it[createdAtEpochMs] = Instant.now().toEpochMilli()
                }
                rowToUser(insert.resultedValues?.firstOrNull())
            }
        } catch (_: ExposedSQLException) {
            null
        }
    }

    fun findByEmail(email: String): UserRecord? {
        return transaction {
            UsersTable
                .selectAll()
                .where { UsersTable.email eq email }
                .limit(1)
                .map(::rowToUser)
                .firstOrNull()
        }
    }

    fun findById(id: Int): UserRecord? {
        return transaction {
            UsersTable
                .selectAll()
                .where { UsersTable.id eq id }
                .limit(1)
                .map(::rowToUser)
                .firstOrNull()
        }
    }

    private fun rowToUser(row: ResultRow?): UserRecord? {
        if (row == null) {
            return null
        }

        return UserRecord(
            id = row[UsersTable.id].value,
            email = row[UsersTable.email],
            passwordHash = row[UsersTable.passwordHash],
            displayName = row[UsersTable.displayName] ?: row[UsersTable.email].substringBefore("@"),
            createdAtEpochMs = row[UsersTable.createdAtEpochMs],
        )
    }
}
