package com.barcodebite.backend

import com.barcodebite.backend.config.AppConfig
import com.barcodebite.backend.config.DatabaseConfig
import com.barcodebite.backend.config.JwtConfig
import com.barcodebite.backend.config.OpenFoodFactsConfig
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ApplicationTest {
    private val json = Json { ignoreUnknownKeys = true }

    private fun testConfig(databaseName: String): AppConfig {
        return AppConfig(
            database = DatabaseConfig(
                url = "jdbc:h2:mem:$databaseName;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                user = "sa",
                password = "",
                driver = "org.h2.Driver",
                maxPoolSize = 2,
            ),
            jwt = JwtConfig(
                secret = "test-secret-very-secure",
                issuer = "test.barcodebite",
                audience = "test-users",
                expiresInHours = 2,
            ),
            openFoodFacts = OpenFoodFactsConfig(
                baseUrl = "http://127.0.0.1:1/api/v2",
                userAgent = "BarcodeBites-Test/1.0",
                timeoutMillis = 25,
            ),
        )
    }

    @Test
    fun healthEndpointReturnsOkStatus() = testApplication {
        application { module(appConfig = testConfig("health")) }

        val response = client.get("/health")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("\"status\":\"ok\""))
    }

    @Test
    fun registerLoginAndGetCurrentUserFlowWorks() = testApplication {
        application { module(appConfig = testConfig("auth-flow")) }

        val registerResponse = client.post("/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"alice@barcodebite.dev","password":"Password123!"}""")
        }
        assertEquals(HttpStatusCode.Created, registerResponse.status)

        val registerJson = json.parseToJsonElement(registerResponse.bodyAsText()).jsonObject
        val accessToken = registerJson["accessToken"]?.jsonPrimitive?.content ?: ""
        assertTrue(accessToken.isNotBlank())

        val duplicateRegisterResponse = client.post("/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"alice@barcodebite.dev","password":"Password123!"}""")
        }
        assertEquals(HttpStatusCode.Conflict, duplicateRegisterResponse.status)

        val meUnauthorized = client.get("/v1/users/me")
        assertEquals(HttpStatusCode.Unauthorized, meUnauthorized.status)

        val meResponse = client.get("/v1/users/me") {
            headers.append(HttpHeaders.Authorization, "Bearer $accessToken")
        }
        assertEquals(HttpStatusCode.OK, meResponse.status)
        val meJson = json.parseToJsonElement(meResponse.bodyAsText()).jsonObject
        assertEquals("alice@barcodebite.dev", meJson["email"]?.jsonPrimitive?.content)
        assertEquals("alice", meJson["displayName"]?.jsonPrimitive?.content)
        assertTrue((meJson["createdAtEpochMs"]?.jsonPrimitive?.longOrNull ?: 0L) > 0L)

        val subscriptionResponse = client.get("/v1/subscriptions/status") {
            headers.append(HttpHeaders.Authorization, "Bearer $accessToken")
        }
        assertEquals(HttpStatusCode.OK, subscriptionResponse.status)
        val subscriptionJson = json.parseToJsonElement(subscriptionResponse.bodyAsText()).jsonObject
        assertEquals("free", subscriptionJson["plan"]?.jsonPrimitive?.content)
        assertEquals(false, subscriptionJson["isActive"]?.jsonPrimitive?.boolean)
        assertEquals(null, subscriptionJson["expiresAtEpochMs"]?.jsonPrimitive?.longOrNull)
        val entitlements = subscriptionJson["entitlements"]?.jsonObject ?: error("missing entitlements")
        assertEquals(false, entitlements["compare"]?.jsonPrimitive?.boolean)
        assertEquals(false, entitlements["advanced_analysis"]?.jsonPrimitive?.boolean)
        assertEquals(false, entitlements["unlimited_scans"]?.jsonPrimitive?.boolean)

        val verifyResponse = client.post("/v1/subscriptions/verify") {
            headers.append(HttpHeaders.Authorization, "Bearer $accessToken")
            contentType(ContentType.Application.Json)
            setBody("""{"plan":"premium_monthly","purchaseToken":"tok_test_123"}""")
        }
        assertEquals(HttpStatusCode.OK, verifyResponse.status)
        val verifiedJson = json.parseToJsonElement(verifyResponse.bodyAsText()).jsonObject
        assertEquals("premium_monthly", verifiedJson["plan"]?.jsonPrimitive?.content)
        assertEquals(true, verifiedJson["isActive"]?.jsonPrimitive?.boolean)

        val restoreResponse = client.post("/v1/subscriptions/restore") {
            headers.append(HttpHeaders.Authorization, "Bearer $accessToken")
            contentType(ContentType.Application.Json)
            setBody("""{"purchaseToken":"tok_test_123"}""")
        }
        assertEquals(HttpStatusCode.OK, restoreResponse.status)
        val restoreJson = json.parseToJsonElement(restoreResponse.bodyAsText()).jsonObject
        assertEquals("premium_monthly", restoreJson["plan"]?.jsonPrimitive?.content)
        assertEquals(true, restoreJson["isActive"]?.jsonPrimitive?.boolean)

        val invalidLoginResponse = client.post("/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"alice@barcodebite.dev","password":"WrongPass123"}""")
        }
        assertEquals(HttpStatusCode.Unauthorized, invalidLoginResponse.status)

        val loginResponse = client.post("/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"alice@barcodebite.dev","password":"Password123!"}""")
        }
        assertEquals(HttpStatusCode.OK, loginResponse.status)
        assertTrue(loginResponse.bodyAsText().contains("\"accessToken\""))
    }

    @Test
    fun productAndAnalysisEndpointsUsePersistedData() = testApplication {
        application { module(appConfig = testConfig("product-analysis")) }

        val barcode = "8690504012345"
        val createResponse = client.post("/v1/products") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "barcode":"$barcode",
                  "name":"Sugary Snack",
                  "brand":"Snack Co",
                  "ingredients":"Sugar, Palm Oil, Wheat Flour, Glucose Syrup, Emulsifier, Flavoring, Color",
                  "additives":["e330","e621","e950"],
                  "nutrition":{
                    "calories":450.0,
                    "protein":5.0,
                    "carbohydrates":70.0,
                    "fat":18.0,
                    "sugar":30.0,
                    "salt":1.0
                  }
                }
                """.trimIndent(),
            )
        }
        assertEquals(HttpStatusCode.Created, createResponse.status)

        val productResponse = client.get("/v1/products/$barcode")
        assertEquals(HttpStatusCode.OK, productResponse.status)
        val productJson = json.parseToJsonElement(productResponse.bodyAsText()).jsonObject
        assertEquals("Sugary Snack", productJson["name"]?.jsonPrimitive?.content)
        val additiveJson = productJson["additives"]?.jsonArray ?: error("missing additives")
        assertEquals(3, additiveJson.size)
        assertEquals("e621", additiveJson[1].jsonObject["code"]?.jsonPrimitive?.content)
        assertEquals("Monosodium glutamate", additiveJson[1].jsonObject["name"]?.jsonPrimitive?.content)
        assertEquals("High", additiveJson[1].jsonObject["riskLevel"]?.jsonPrimitive?.content)

        val response = client.post("/v1/analysis") {
            contentType(ContentType.Application.Json)
            setBody("""{"barcode":"$barcode"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val analysisJson = json.parseToJsonElement(response.bodyAsText()).jsonObject
        assertEquals("E", analysisJson["grade"]?.jsonPrimitive?.content)
        assertEquals(barcode, analysisJson["barcode"]?.jsonPrimitive?.content)
        assertEquals(25, analysisJson["cleanLabelScore"]?.jsonPrimitive?.content?.toInt())
        assertEquals("Ultra-Processed", analysisJson["cleanLabelVerdict"]?.jsonPrimitive?.content)
        assertEquals(true, analysisJson["isJunkFood"]?.jsonPrimitive?.boolean)
        val junkFoodReasons = analysisJson["junkFoodReasons"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList()
        assertTrue(junkFoodReasons.isNotEmpty())
        assertTrue(junkFoodReasons.any { it.contains("sugar", ignoreCase = true) })

        val healthyBarcode = "8690504999999"
        val healthyCreate = client.post("/v1/products") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "barcode":"$healthyBarcode",
                  "name":"Pure Oat Mix",
                  "brand":"Nature Co",
                  "ingredients":"Whole oats, Hazelnut, Honey",
                  "additives":[],
                  "nutrition":{
                    "calories":320.0,
                    "protein":12.0,
                    "carbohydrates":51.0,
                    "fat":8.0,
                    "sugar":4.0,
                    "salt":0.1
                  }
                }
                """.trimIndent(),
            )
        }
        assertEquals(HttpStatusCode.Created, healthyCreate.status)

        val healthyAnalysis = client.post("/v1/analysis") {
            contentType(ContentType.Application.Json)
            setBody("""{"barcode":"$healthyBarcode"}""")
        }

        assertEquals(HttpStatusCode.OK, healthyAnalysis.status)
        val healthyAnalysisJson = json.parseToJsonElement(healthyAnalysis.bodyAsText()).jsonObject
        assertEquals(93, healthyAnalysisJson["cleanLabelScore"]?.jsonPrimitive?.content?.toInt())
        assertEquals("Excellent", healthyAnalysisJson["cleanLabelVerdict"]?.jsonPrimitive?.content)
        assertEquals(false, healthyAnalysisJson["isJunkFood"]?.jsonPrimitive?.boolean)
        val healthyReasons = healthyAnalysisJson["junkFoodReasons"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList()
        assertTrue(healthyReasons.isEmpty())

        val compareResponse = client.post("/v1/analysis/compare") {
            contentType(ContentType.Application.Json)
            setBody("""{"firstBarcode":"$barcode","secondBarcode":"$healthyBarcode"}""")
        }
        assertEquals(HttpStatusCode.OK, compareResponse.status)
        val compareJson = json.parseToJsonElement(compareResponse.bodyAsText()).jsonObject
        assertEquals(healthyBarcode, compareJson["recommendation"]?.jsonPrimitive?.content)
    }
}
