package com.barcodebite.backend.repository

import com.barcodebite.backend.model.NutritionValues
import com.barcodebite.backend.model.ProductRecord
import com.barcodebite.backend.persistence.table.ProductsTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class ProductRepository {
    fun findByBarcode(barcode: String): ProductRecord? {
        return transaction {
            ProductsTable
                .selectAll()
                .where { ProductsTable.barcode eq barcode }
                .limit(1)
                .map(::rowToProduct)
                .firstOrNull()
        }
    }

    fun upsert(product: ProductRecord): ProductRecord {
        transaction {
            val updatedRows = ProductsTable.update(where = { ProductsTable.barcode eq product.barcode }) {
                it[name] = product.name
                it[brand] = product.brand
                it[calories] = product.nutrition.calories
                it[protein] = product.nutrition.protein
                it[carbohydrates] = product.nutrition.carbohydrates
                it[fat] = product.nutrition.fat
                it[sugar] = product.nutrition.sugar
                it[salt] = product.nutrition.salt
                it[ingredients] = product.ingredients
                it[additivesCsv] = toCsv(product.additives)
            }

            if (updatedRows == 0) {
                ProductsTable.insert {
                    it[barcode] = product.barcode
                    it[name] = product.name
                    it[brand] = product.brand
                    it[calories] = product.nutrition.calories
                    it[protein] = product.nutrition.protein
                    it[carbohydrates] = product.nutrition.carbohydrates
                    it[fat] = product.nutrition.fat
                    it[sugar] = product.nutrition.sugar
                    it[salt] = product.nutrition.salt
                    it[ingredients] = product.ingredients
                    it[additivesCsv] = toCsv(product.additives)
                }
            }
        }

        return product
    }

    private fun rowToProduct(row: ResultRow): ProductRecord {
        return ProductRecord(
            barcode = row[ProductsTable.barcode],
            name = row[ProductsTable.name],
            brand = row[ProductsTable.brand],
            nutrition = NutritionValues(
                calories = row[ProductsTable.calories],
                protein = row[ProductsTable.protein],
                carbohydrates = row[ProductsTable.carbohydrates],
                fat = row[ProductsTable.fat],
                sugar = row[ProductsTable.sugar],
                salt = row[ProductsTable.salt],
            ),
            ingredients = row[ProductsTable.ingredients],
            additives = fromCsv(row[ProductsTable.additivesCsv]),
        )
    }

    private fun toCsv(values: List<String>): String {
        return values
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .joinToString(",")
    }

    private fun fromCsv(value: String): List<String> {
        return value
            .split(',')
            .map { it.trim() }
            .filter { it.isNotBlank() }
    }
}
