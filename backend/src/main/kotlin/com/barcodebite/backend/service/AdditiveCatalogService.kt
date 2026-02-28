package com.barcodebite.backend.service

import java.io.BufferedReader
import java.io.InputStreamReader

data class AdditiveCatalogEntry(
    val code: String,
    val name: String,
    val riskLevel: String,
    val description: String,
)

class AdditiveCatalogService(
    private val resourcePath: String = "data/additives.csv",
) {
    private val catalog: Map<String, AdditiveCatalogEntry> by lazy { loadCatalog() }

    fun catalogSize(): Int = catalog.size

    fun find(code: String): AdditiveCatalogEntry? {
        return catalog[normalizeCode(code)]
    }

    fun resolve(codes: List<String>): List<AdditiveCatalogEntry> {
        return codes.map { code ->
            find(code) ?: AdditiveCatalogEntry(
                code = normalizeCode(code),
                name = "Unknown additive",
                riskLevel = "Unknown",
                description = "No additive catalog entry found for this code.",
            )
        }
    }

    private fun loadCatalog(): Map<String, AdditiveCatalogEntry> {
        val stream = Thread.currentThread().contextClassLoader.getResourceAsStream(resourcePath)
            ?: error("Missing additive catalog resource: $resourcePath")

        BufferedReader(InputStreamReader(stream)).use { reader ->
            return reader
                .lineSequence()
                .drop(1)
                .filter { it.isNotBlank() }
                .map(::parseLine)
                .associateBy { it.code }
        }
    }

    private fun parseLine(line: String): AdditiveCatalogEntry {
        val parts = line.split(',', limit = 4)
        require(parts.size == 4) { "Invalid additive catalog line: $line" }

        return AdditiveCatalogEntry(
            code = normalizeCode(parts[0]),
            name = parts[1].trim(),
            riskLevel = parts[2].trim(),
            description = parts[3].trim(),
        )
    }

    private fun normalizeCode(code: String): String {
        return code.trim().lowercase()
    }
}
