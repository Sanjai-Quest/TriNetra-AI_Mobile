package com.trinetra.ai.identity

data class ProductIdentifier(
    val orderId: String,
    val packageId: String,
    val productName: String
)

interface ProductIdentifierSource {
    fun resolve(rawValue: String): ProductIdentifier?
}

/** Offline prototype lookup. RFID can replace this source without changing evidence consumers. */
class QrProductIdentifierSource : ProductIdentifierSource {
    override fun resolve(rawValue: String): ProductIdentifier? {
        val orderId = rawValue.trim().uppercase()
        if (!Regex("ORD-[0-9]{5}").matches(orderId)) return null
        return when (orderId) {
            "ORD-98402" -> ProductIdentifier(orderId, "TRN-PKG-7729-A", "Smartphone")
            else -> null
        }
    }
}
