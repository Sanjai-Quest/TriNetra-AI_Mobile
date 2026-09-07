package com.trinetra.ai.identity

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProductIdentifierTest {
    private val source = QrProductIdentifierSource()

    @Test
    fun resolvesCanonicalOrderOffline() {
        val result = source.resolve("  ord-98402 ")

        assertEquals("ORD-98402", result?.orderId)
        assertEquals("TRN-PKG-7729-A", result?.packageId)
        assertEquals("Smartphone", result?.productName)
    }

    @Test
    fun rejectsUnknownOrMalformedValues() {
        assertNull(source.resolve(""))
        assertNull(source.resolve("TRN-PKG-7729-A"))
        assertNull(source.resolve("ORD-12345"))
    }
}
