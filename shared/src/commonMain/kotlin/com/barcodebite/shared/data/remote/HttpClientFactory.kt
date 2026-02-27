package com.barcodebite.shared.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {
    fun create(engine: HttpClientEngine? = null): HttpClient {
        return if (engine == null) {
            HttpClient {
                install(ContentNegotiation) {
                    json(defaultJson())
                }
            }
        } else {
            HttpClient(engine) {
                install(ContentNegotiation) {
                    json(defaultJson())
                }
            }
        }
    }

    private fun defaultJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = false
    }
}
