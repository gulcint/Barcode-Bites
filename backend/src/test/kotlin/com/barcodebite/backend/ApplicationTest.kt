package com.barcodebite.backend

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {

    @Test
    fun healthEndpointReturnsOkStatus() = testApplication {
        application { module() }

        val response = client.get("/health")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("\"status\":\"ok\""))
    }

    @Test
    fun productEndpointReturnsBarcodePayload() = testApplication {
        application { module() }

        val barcode = "8690504012345"
        val response = client.get("/v1/products/$barcode")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains(barcode))
    }

    @Test
    fun analysisEndpointReturnsScoreAndGrade() = testApplication {
        application { module() }

        val response = client.post("/v1/analysis") {
            contentType(ContentType.Application.Json)
            setBody("""{"barcode":"8690504012345"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("\"score\""))
        assertTrue(response.bodyAsText().contains("\"grade\""))
    }
}
