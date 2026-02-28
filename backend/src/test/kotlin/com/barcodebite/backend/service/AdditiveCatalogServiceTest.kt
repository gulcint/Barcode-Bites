package com.barcodebite.backend.service

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AdditiveCatalogServiceTest {
    @Test
    fun catalogLoadsMoreThanFiveHundredEntriesAndResolvesKnownCodes() {
        val service = AdditiveCatalogService()

        assertTrue(service.catalogSize() >= 500)

        val msg = service.find("e621")
        assertNotNull(msg)
        assertEquals("Monosodium glutamate", msg.name)
        assertEquals("High", msg.riskLevel)

        val sweetener = service.find("E950")
        assertNotNull(sweetener)
        assertEquals("Acesulfame K", sweetener.name)
        assertEquals("High", sweetener.riskLevel)
    }
}
